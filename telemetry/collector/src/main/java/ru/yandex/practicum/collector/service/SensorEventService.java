package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.collector.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.collector.model.sensor.LightSensorEvent;
import ru.yandex.practicum.collector.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.collector.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.collector.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventService {

    private final Producer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topic.sensor-events}")
    private String sensorEventsTopic;

    public void processEvent(SensorEvent event) {
        SensorEventAvro avro = mapToAvro(event);
        log.info("SensorEvent отправляется в Kafka с payload: {}", avro.getPayload().getClass().getSimpleName());
        kafkaProducer.send(new ProducerRecord<>(sensorEventsTopic, avro.getId(), avro));
    }

    private SensorEventAvro mapToAvro(SensorEvent event) {
        Instant timestamp = event.getTimestamp() != null
                ? event.getTimestamp()
                : Instant.now();

        SpecificRecord payload = switch (event.getType()) {
            case LIGHT_SENSOR_EVENT -> LightSensorAvro.newBuilder()
                    .setLinkQuality(((LightSensorEvent) event).getLinkQuality())
                    .setLuminosity(((LightSensorEvent) event).getLuminosity())
                    .build();
            case MOTION_SENSOR_EVENT -> MotionSensorAvro.newBuilder()
                    .setLinkQuality(((MotionSensorEvent) event).getLinkQuality())
                    .setMotion(((MotionSensorEvent) event).isMotion())
                    .setVoltage(((MotionSensorEvent) event).getVoltage())
                    .build();
            case TEMPERATURE_SENSOR_EVENT -> TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(((TemperatureSensorEvent) event).getTemperatureC())
                    .setTemperatureF(((TemperatureSensorEvent) event).getTemperatureF())
                    .build();
            case CLIMATE_SENSOR_EVENT -> ClimateSensorAvro.newBuilder()
                    .setTemperatureC(((ClimateSensorEvent) event).getTemperatureC())
                    .setHumidity(((ClimateSensorEvent) event).getHumidity())
                    .setCo2Level(((ClimateSensorEvent) event).getCo2Level())
                    .build();
            case SWITCH_SENSOR_EVENT -> SwitchSensorAvro.newBuilder()
                    .setState(((SwitchSensorEvent) event).isState())
                    .build();
        };

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();
    }


    public void handleSensorEvent(SensorEventProto proto) {
        SensorEvent event;

        Instant timestamp = Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        );

        switch (proto.getPayloadCase()) {
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent t = new TemperatureSensorEvent();
                t.setTemperatureC(proto.getTemperatureSensorEvent().getTemperatureC());
                t.setTemperatureF(proto.getTemperatureSensorEvent().getTemperatureF());
                event = t;
            }
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent m = new MotionSensorEvent();
                m.setLinkQuality(proto.getMotionSensorEvent().getLinkQuality());
                m.setMotion(proto.getMotionSensorEvent().getMotion());
                m.setVoltage(proto.getMotionSensorEvent().getVoltage());
                event = m;
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent l = new LightSensorEvent();
                l.setLinkQuality(proto.getLightSensorEvent().getLinkQuality());
                l.setLuminosity(proto.getLightSensorEvent().getLuminosity());
                event = l;
            }
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent c = new ClimateSensorEvent();
                c.setTemperatureC(proto.getClimateSensorEvent().getTemperatureC());
                c.setHumidity(proto.getClimateSensorEvent().getHumidity());
                c.setCo2Level(proto.getClimateSensorEvent().getCo2Level());
                event = c;
            }
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent s = new SwitchSensorEvent();
                s.setState(proto.getSwitchSensorEvent().getState());
                event = s;
            }
            default -> {
                log.warn("Неизвестный payload: {}", proto.getPayloadCase());
                return;
            }
        }

        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(timestamp);

        processEvent(event);
    }
}