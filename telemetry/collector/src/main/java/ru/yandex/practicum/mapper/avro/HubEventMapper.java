package ru.yandex.practicum.mapper.avro;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hubs.*;

import java.util.Objects;

@Slf4j
public class HubEventMapper {

    public static HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        SpecificRecordBase hubEventPayloadAvro = toHubEventPayloadAvro(hubEvent);
        HubEventAvro build = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(hubEventPayloadAvro)
                .build();
        log.info(build.toString());
        return build;
    }

    public static SpecificRecordBase toHubEventPayloadAvro(HubEvent hubEvent) {
        Objects.requireNonNull(hubEvent.getType(), "Hub event type cannot be null");

        return switch (hubEvent.getType()) {
            case DEVICE_ADDED -> mapDeviceAdded((DeviceAddedEvent) hubEvent);
            case DEVICE_REMOVED -> mapDeviceRemoved((DeviceRemovedEvent) hubEvent);
            case SCENARIO_ADDED -> mapScenarioAdded((ScenarioAddedEvent) hubEvent);
            case SCENARIO_REMOVED -> mapScenarioRemoved((ScenarioRemovedEvent) hubEvent);
            default -> throw new IllegalStateException("Invalid payload: " + hubEvent.getType());
        };
    }

    private static DeviceAddedEventAvro mapDeviceAdded(DeviceAddedEvent event) {
        DeviceAddedEventAvro build = DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(toDeviceTypeAvro(event.getDeviceType()))
                .build();
        log.info(build.toString());
        return build;
    }

    private static DeviceRemovedEventAvro mapDeviceRemoved(DeviceRemovedEvent event) {
        DeviceRemovedEventAvro build = DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
        log.info(build.toString());
        return build;
    }

    private static ScenarioAddedEventAvro mapScenarioAdded(ScenarioAddedEvent event) {
        ScenarioAddedEventAvro build = ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setActions(event.getActions().stream().map(HubEventMapper::toDeviceActionAvro).toList())
                .setConditions(event.getConditions().stream().map(HubEventMapper::toScenarioConditionAvro).toList())
                .build();
        log.info(build.toString());
        return build;
    }

    private static ScenarioRemovedEventAvro mapScenarioRemoved(ScenarioRemovedEvent event) {
        ScenarioRemovedEventAvro build = ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
        log.info(build.toString());
        return build;
    }

    public static DeviceTypeAvro toDeviceTypeAvro(DeviceType deviceType) {
        return DeviceTypeAvro.valueOf(deviceType.name());
    }

    public static DeviceActionAvro toDeviceActionAvro(DeviceAction deviceAction) {
        DeviceActionAvro build = DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(toActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
        log.info(build.toString());
        return build;
    }

    public static ActionTypeAvro toActionTypeAvro(ActionType actionType) {
        return ActionTypeAvro.valueOf(actionType.name());
    }

    public static ConditionTypeAvro toConditionTypeAvro(ConditionType conditionType) {
        return ConditionTypeAvro.valueOf(conditionType.name());
    }

    public static ConditionOperationAvro toConditionOperationAvro(ConditionOperation conditionOperation) {
        return ConditionOperationAvro.valueOf(conditionOperation.name());
    }

    public static ScenarioConditionAvro toScenarioConditionAvro(ScenarioCondition scenarioCondition) {
        ScenarioConditionAvro build = ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(toConditionTypeAvro(scenarioCondition.getConditionType()))
                .setOperation(toConditionOperationAvro(scenarioCondition.getConditionOperation()))
                .setValue(scenarioCondition.getValue())
                .build();
        log.info(build.toString());
        return build;
    }
}
