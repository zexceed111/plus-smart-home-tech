package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.EnumMapper;
import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.hub.DeviceAction;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioCondition;
import ru.yandex.practicum.model.hub.enums.HubEventType;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent _event = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setConditions(mapConditionsToAvro(_event.getConditions()))
                .setActions(mapActionsToAvro(_event.getActions()))
                .build();
    }

    private List<ScenarioConditionAvro> mapConditionsToAvro(List<ScenarioCondition> conditions) {
        List<ScenarioConditionAvro> listAvro = new ArrayList<>();
        for (ScenarioCondition condition : conditions) {
            ScenarioConditionAvro conditionAvro = ScenarioConditionAvro.newBuilder()
                    .setSensorId(condition.getSensorId())
                    .setType(EnumMapper.mapEnum(condition.getType(), ConditionTypeAvro.class))
                    .setOperation(EnumMapper.mapEnum(condition.getOperation(), ConditionOperationAvro.class))
                    .setValue(condition.getValue())
                    .build();
            listAvro.add(conditionAvro);
        }
        return listAvro;
    }

    private List<DeviceActionAvro> mapActionsToAvro(List<DeviceAction> actions) {
        List<DeviceActionAvro> listAvro = new ArrayList<>();
        for (DeviceAction action : actions) {
            DeviceActionAvro actionAvro = DeviceActionAvro.newBuilder()
                    .setSensorId(action.getSensorId())
                    .setType(EnumMapper.mapEnum(action.getType(), ActionTypeAvro.class))
                    .setValue(action.getValue())
                    .build();
            listAvro.add(actionAvro);
        }
        return listAvro;
    }

}
