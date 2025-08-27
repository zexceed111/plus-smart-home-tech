package ru.yandex.practicum.event.mapper.sensor_converter;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.sensor_event.ClimateSensorEvent;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import org.apache.avro.specific.SpecificRecordBase;

@Component
public class ClimateSensorEventConverter implements SensorEventConverter<ClimateSensorEvent> {
    @Override
    public boolean supports(SensorEvent event) {
        return event instanceof ClimateSensorEvent;
    }

    @Override
    public SpecificRecordBase toAvro(ClimateSensorEvent event) {
        return ClimateSensorAvro.newBuilder()
                .setCo2Level(event.getCo2Level())
                .setHumidity(event.getHumidity())
                .setTemperatureC(event.getTemperatureC())
                .build();
    }
}
