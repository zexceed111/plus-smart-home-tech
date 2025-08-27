package ru.yandex.practicum.event.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;
import ru.yandex.practicum.event.model.sensor_event.TemperatureSensorEvent;
import ru.yandex.practicum.event.mapper.SensorEventConverter;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorEventConverter implements SensorEventConverter<TemperatureSensorEvent> {
    @Override
    public boolean supports(SensorEvent event) {
        return event instanceof TemperatureSensorEvent;
    }

    @Override
    public SpecificRecordBase toAvro(TemperatureSensorEvent event) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }
}

