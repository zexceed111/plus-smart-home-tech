package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConsumerConfig;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.service.AnalyzerService;
import ru.yandex.practicum.service.HubEventService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {
    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final AnalyzerService analyzerService;
    private final HubEventService hubEventService;


    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(kafkaConsumerConfig.getKafkaConsumerProperties().getSensorSnapshotsTopic()));
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer
                        .poll(Duration.ofMillis(kafkaConsumerConfig.getKafkaConsumerProperties()
                                .getSnapshotConsumer().getAttemptTimeout()));
                int count = 0;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignores) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки снимка состояния ", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorsSnapshotAvro> consumerRecord) throws InterruptedException {
        log.info("analyzer handleRecord {}", consumerRecord.value());
        List<Scenario> scenarios = analyzerService.getScenariosBySnapshot(consumerRecord.value());
        log.info("==> found scenarios for execute {}", scenarios.size());
        for (Scenario scenario: scenarios) {
            hubEventService.sendActionsByScenario(scenario);
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> consumerRecord,
                               int count,
                               Consumer<String, SensorsSnapshotAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                new OffsetAndMetadata(consumerRecord.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}
