package ru.yandex.practicum.mapper.proto.hubs;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.model.hubs.*;

import java.time.Instant;

@Component
public class ScenarioAddedEventMapper implements HubEventProtoMapper {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubEvent map(HubEventProto event) {
        ScenarioAddedEventProto hubEvent = event.getScenarioAdded();
        return ScenarioAddedEvent.builder()
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .name(hubEvent.getName())
                .actions(hubEvent.getActionList().stream().map(this::map).toList())
                .conditions(hubEvent.getConditionList().stream().map(this::map).toList())
                .build();
    }

    private DeviceAction map(DeviceActionProto deviceActionProto) {
        return DeviceAction.builder()
                .sensorId(deviceActionProto.getSensorId())
                .type(ActionType.valueOf(deviceActionProto.getType().name()))
                .value(deviceActionProto.hasValue() ? deviceActionProto.getValue() : null)
                .build();
    }

    private ScenarioCondition map(ScenarioConditionProto scenarioConditionProto) {
        return ScenarioCondition.builder()
                .sensorId(scenarioConditionProto.getSensorId())
                .conditionType(ConditionType.valueOf(scenarioConditionProto.getType().name()))
                .conditionOperation(ConditionOperation.valueOf(scenarioConditionProto.getOperation().name()))
                .value(scenarioConditionProto.getValueCase() == ScenarioConditionProto.ValueCase.BOOL_VALUE
                        ? scenarioConditionProto.getBoolValue()
                        : scenarioConditionProto.getIntValue())
                .build();
    }
}
