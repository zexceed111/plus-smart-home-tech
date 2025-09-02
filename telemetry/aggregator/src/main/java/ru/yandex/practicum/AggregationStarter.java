package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;
import ru.yandex.practicum.service.SnapshotService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final SnapshotService snapshotService;
    private final KafkaClient kafkaClient;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Value(value = "${sensorEventTopic}")
    private String sensorTopic;

    @Value(value = "${hubEventTopic}")
    private String hubTopic;


    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        try (Consumer<String, SensorEventAvro> consumer = kafkaClient.getConsumer()) {
            consumer.subscribe(List.of(sensorTopic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = snapshotService.getSensorEvents();
                int count = 0;
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }

        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        }

    }

    private void handleRecord(ConsumerRecord<String, SensorEventAvro> consumerRecord) {
        Optional<SensorsSnapshotAvro> snapshotAvro = snapshotService.updateState(consumerRecord.value());
        snapshotAvro.ifPresent(snapshotService::sendSensorSnapshot);
    }

    private void manageOffsets(ConsumerRecord<String, SensorEventAvro> record, int count, Consumer<String, SensorEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
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