package ru.yandex.practicum.event.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.mapper.hub.HubEventConverter;
import ru.yandex.practicum.event.mapper.sensor.SensorEventConverter;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.List;

@Component
public class EventMapper {

    private final List<SensorEventConverter<? extends SensorEvent>> sensorConverters;
    private final List<HubEventConverter<? extends HubEvent>> hubConverters;

    public EventMapper(List<SensorEventConverter<? extends SensorEvent>> sensorConverters,
                       List<HubEventConverter<? extends HubEvent>> hubConverters) {
        this.sensorConverters = sensorConverters;
        this.hubConverters = hubConverters;
    }

    public SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {
        SpecificRecordBase payload = sensorConverters.stream()
                .filter(converter -> converter.supports(sensorEvent))
                .findFirst()
                .map(converter -> ((SensorEventConverter<SensorEvent>) converter).toAvro(sensorEvent))
                .orElseThrow(() -> new RuntimeException("Не найден конвертер для события датчика: " + sensorEvent));

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        SpecificRecordBase payload = hubConverters.stream()
                .filter(converter -> converter.supports(hubEvent))
                .findFirst()
                .map(converter -> ((HubEventConverter<HubEvent>) converter).toAvro(hubEvent))
                .orElseThrow(() -> new RuntimeException("Не найден конвертер для события хаба: " + hubEvent));

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
