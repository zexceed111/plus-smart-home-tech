package ru.yandex.practicum.event.mapper.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.hub_event.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import org.apache.avro.specific.SpecificRecordBase;

@Component
public class ScenarioRemovedEventConverter implements HubEventConverter<ScenarioRemovedEvent> {

    @Override
    public boolean supports(HubEvent event) {
        return event instanceof ScenarioRemovedEvent;
    }

    @Override
    public SpecificRecordBase toAvro(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }
}
