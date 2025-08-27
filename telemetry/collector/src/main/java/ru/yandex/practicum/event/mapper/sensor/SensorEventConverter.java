package ru.yandex.practicum.event.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;

public interface SensorEventConverter<T extends SensorEvent> {
    boolean supports(SensorEvent event);

    SpecificRecordBase toAvro(T event);
}

