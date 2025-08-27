package ru.yandex.practicum.event.mapper.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.event.model.hub_event.HubEvent;

public interface HubEventConverter<T extends HubEvent> {
    boolean supports(HubEvent event);

    SpecificRecordBase toAvro(T event);
}
