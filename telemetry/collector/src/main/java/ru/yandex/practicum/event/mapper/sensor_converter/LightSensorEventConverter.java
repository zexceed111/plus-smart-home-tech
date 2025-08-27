package ru.yandex.practicum.event.mapper.sensor_converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor_event.LightSensorEvent;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventConverter implements SensorEventConverter<LightSensorEvent> {
    @Override
    public boolean supports(SensorEvent event){
        return event instanceof LightSensorEvent;
    }
    @Override
    public SpecificRecordBase toAvro(LightSensorEvent event){
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }
}
