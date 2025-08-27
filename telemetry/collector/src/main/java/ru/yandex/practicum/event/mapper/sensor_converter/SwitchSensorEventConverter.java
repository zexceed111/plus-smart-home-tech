package ru.yandex.practicum.event.mapper.sensor_converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;
import ru.yandex.practicum.event.model.sensor_event.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventConverter implements SensorEventConverter<SwitchSensorEvent> {
    @Override
    public boolean supports(SensorEvent event){
        return event instanceof SwitchSensorEvent;
    }
    @Override
    public SpecificRecordBase toAvro(SwitchSensorEvent event){
        return SwitchSensorAvro.newBuilder()
                .setState(event.isState())
                .build();
    }
}
