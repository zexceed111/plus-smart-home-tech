package ru.yandex.practicum.event.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor_event.MotionSensorEvent;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorEventConverter implements SensorEventConverter<MotionSensorEvent> {
    @Override
    public boolean supports(SensorEvent event) {
        return event instanceof MotionSensorEvent;
    }

    @Override
    public SpecificRecordBase toAvro(MotionSensorEvent event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.isMotion())
                .setVoltage(event.getVoltage())
                .build();
    }
}