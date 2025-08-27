package ru.yandex.practicum.event.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.kafka_client.KafkaClient;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final KafkaClient kafkaClient;
    private final EventMapper eventMapper; // ✅ инжектим бин

    private static final String SENSORS_TOPIC = "telemetry.sensors.v1";
    private static final String HUB_TOPIC = "telemetry.hubs.v1";

    public void sendSensorEvent(SensorEvent event) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                SENSORS_TOPIC,
                null,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                eventMapper.toSensorEventAvro(event) // ✅ не статик
        );

        kafkaClient.getProducer().send(record, (RecordMetadata metadata, Exception exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке SensorEvent: topic={}, key={}, payload={}",
                        record.topic(), record.key(), record.value(), exception);
            } else {
                log.info("SensorEvent отправлен: topic={}, partition={}, offset={}, key={}",
                        metadata.topic(), metadata.partition(), metadata.offset(), record.key());
            }
        });
    }

    public void sendHubEvent(HubEvent event) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                HUB_TOPIC,
                null,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                eventMapper.toHubEventAvro(event) // ✅ не статик
        );

        kafkaClient.getProducer().send(record, (RecordMetadata metadata, Exception exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке HubEvent: topic={}, key={}, payload={}",
                        record.topic(), record.key(), record.value(), exception);
            } else {
                log.info("HubEvent отправлен: topic={}, partition={}, offset={}, key={}",
                        metadata.topic(), metadata.partition(), metadata.offset(), record.key());
            }
        });
    }

    @PreDestroy
    public void stop() {
        kafkaClient.stop();
    }
}
