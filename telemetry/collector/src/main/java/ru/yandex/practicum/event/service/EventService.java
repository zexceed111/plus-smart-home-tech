package ru.yandex.practicum.event.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.kafka_client.KafkaClient;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;

@Service
@RequiredArgsConstructor
public class EventService {

    private final KafkaClient kafkaClient;

    private static final String SENSORS_TOPIC = "telemetry.sensors.v1";
    private static final String HUB_TOPIC = "telemetry.hubs.v1";

    public void sendSensorEvent(SensorEvent event) {
        kafkaClient.getProducer().send(new ProducerRecord<>(
                SENSORS_TOPIC,
                null,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                EventMapper.toSensorEventAvro(event))
        );
    }

    public void sendHubEvent(HubEvent event) {
        kafkaClient.getProducer().send(new ProducerRecord<>(
                HUB_TOPIC,
                null,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                EventMapper.toHubEventAvro(event))
        );
    }

    @PreDestroy
    public void stop() {
        kafkaClient.stop();
    }

}
