package ru.yandex.practicum.telemetry.analyzer.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioEvaluationService;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final ScenarioEvaluationService scenarioService;

    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private volatile boolean running = true;

    public void start() {
        consumer.subscribe(Collections.singletonList("telemetry.snapshots.v1"));
        log.info("🟢 SnapshotProcessor слушает telemetry.snapshots.v1");

        try {
            while (running) {
                try {
                    ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(500));
                    for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                        SensorsSnapshotAvro snapshot = record.value();
                        try {
                            scenarioService.evaluateAndExecute(snapshot);
                        } catch (Exception e) {
                            log.error("Ошибка при анализе снапшота: {}", e.getMessage(), e);
                        }

                        TopicPartition partition = new TopicPartition(record.topic(), record.partition());
                        offsets.put(partition, new OffsetAndMetadata(record.offset() + 1));
                    }
                    if (!offsets.isEmpty()) {
                        consumer.commitSync(offsets);
                        offsets.clear();
                    }
                } catch (WakeupException e) {
                    if (running) throw e;
                    break;
                }
            }
        } finally {
            try {
                if (!offsets.isEmpty()) {
                    consumer.commitSync(offsets);
                }
            } catch (Exception e) {
                log.warn("⚠️ Ошибка при финальном коммите оффсетов", e);
            } finally {
                consumer.close();
                log.info("🟢 Kafka consumer (snapshots) закрыт");
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        consumer.wakeup();
    }
}