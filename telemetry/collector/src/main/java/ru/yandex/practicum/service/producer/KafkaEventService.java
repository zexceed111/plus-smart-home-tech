package ru.yandex.practicum.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.mapper.avro.HubEventMapper;
import ru.yandex.practicum.mapper.avro.SensorEventMapper;
import ru.yandex.practicum.model.sensors.SensorEvent;
import ru.yandex.practicum.model.hubs.HubEvent;
import ru.yandex.practicum.service.EventService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventService implements EventService {
    private final Producer<String, SpecificRecordBase> producer;
    private final KafkaConfig kafkaConfig;

    @Override
    public void collectSensorEvent(SensorEvent sensorEvent) {
        send(kafkaConfig.getKafkaProperties().getSensorEventsTopic(),
                sensorEvent.getHubId(),
                sensorEvent.getTimestamp().toEpochMilli(),
                SensorEventMapper.toSensorEventAvro(sensorEvent));
    }

    @Override
    public void collectHubEvent(HubEvent hubEvent) {
        send(kafkaConfig.getKafkaProperties().getHubEventsTopic(),
                hubEvent.getHubId(),
                hubEvent.getTimestamp().toEpochMilli(),
                HubEventMapper.toHubEventAvro(hubEvent));
    }

    private void send(String topic, String key, Long timestamp, SpecificRecordBase specificRecordBase) {
        log.info("Sending {} to {}", specificRecordBase, topic);
        ProducerRecord<String, SpecificRecordBase> rec = new ProducerRecord<>(
                topic,
                null,
                timestamp,
                key,
                specificRecordBase);
        producer.send(rec);
    }
}
