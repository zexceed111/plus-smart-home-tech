package ru.yandex.practicum.event.mapper.hub_converter;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.hub_event.device.DeviceRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import org.apache.avro.specific.SpecificRecordBase;

@Component
public class DeviceRemovedEventConverter implements HubEventConverter<DeviceRemovedEvent> {

    @Override
    public boolean supports(HubEvent event) {
        return event instanceof DeviceRemovedEvent;
    }

    @Override
    public SpecificRecordBase toAvro(DeviceRemovedEvent event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }
}
