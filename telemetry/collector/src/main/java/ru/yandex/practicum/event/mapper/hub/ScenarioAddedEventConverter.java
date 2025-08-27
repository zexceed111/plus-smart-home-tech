package ru.yandex.practicum.event.mapper.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.mapper.HubEventConverter;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.hub_event.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import org.apache.avro.specific.SpecificRecordBase;

import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventConverter implements HubEventConverter<ScenarioAddedEvent> {

    @Override
    public boolean supports(HubEvent event) {
        return event instanceof ScenarioAddedEvent;
    }

    @Override
    public SpecificRecordBase toAvro(ScenarioAddedEvent event) {
        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setActions(event.getActions().stream()
                        .map(a -> DeviceActionAvro.newBuilder()
                                .setValue(a.getValue())
                                .setSensorId(a.getSensorId())
                                .setType(ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro.valueOf(a.getType().name()))
                                .build())
                        .collect(Collectors.toList()))
                .setConditions(event.getConditions().stream()
                        .map(c -> ScenarioConditionAvro.newBuilder()
                                .setValue(c.getValue())
                                .setSensorId(c.getSensorId())
                                .setType(ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.valueOf(c.getType().name()))
                                .setOperation(ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro.valueOf(c.getOperation().name()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
