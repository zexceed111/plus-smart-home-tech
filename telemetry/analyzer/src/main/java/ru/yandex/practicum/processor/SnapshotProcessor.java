package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.handler.snapshot.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;
import ru.yandex.practicum.kafka_client.KafkaProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final KafkaClient kafkaClient;
    private final KafkaProperties kafkaProperties;
    private final SnapshotHandler snapshotHandler;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Override
    public void run() {
        log.info("Запущен SnapshotProcessor");
        try (Consumer<String, SensorsSnapshotAvro> snapshotConsumer = kafkaClient.getSnapshotConsumer()) {

            while (true) {
                Long pollTimeout = kafkaProperties.getSnapshotConsumer().getPollTimeoutSec();
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotConsumer.poll(Duration.ofMillis(pollTimeout));

                int count = 0;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    handleRecord(record.value());
                    manageOffsets(record, count, snapshotConsumer);
                    count++;
                }
                snapshotConsumer.commitAsync();
            }

        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшота в analyzer", e);
        }
    }

    private void handleRecord(SensorsSnapshotAvro sensorsSnapshot) {
        log.info("Обработка SensorSnapshot: {}", sensorsSnapshot);
        snapshotHandler.handle(sensorsSnapshot);
        log.info("Завершена обработка SensorSnapshot: {}", sensorsSnapshot);
    }

    private void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> record, int count, Consumer<String, SensorsSnapshotAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );
        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов в SnapshotProcessor: {}", offsets, exception);
                }
            });
        }
    }

}