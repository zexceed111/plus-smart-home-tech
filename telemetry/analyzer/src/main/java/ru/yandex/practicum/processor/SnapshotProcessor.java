package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.handler.snapshot.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;
import ru.yandex.practicum.kafka_client.KafkaProperties;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final KafkaClient kafkaClient;
    private final KafkaProperties kafkaProperties;
    private final SnapshotHandler snapshotHandler;
    private final OffsetManager<SensorsSnapshotAvro> offsetManager;

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
                    offsetManager.manageOffsets(record, count, kafkaProperties.getSnapshotConsumer().getAutoCommitRecordCount(), snapshotConsumer);
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

}