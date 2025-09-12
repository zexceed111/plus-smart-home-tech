package ru.yandex.practicum.event.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final EventService eventService;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        eventService.sendHubEvent(mapFromProtoToAvro(event));
    }

    private HubEventAvro mapFromProtoToAvro(HubEventProto hubEventProto) {
        ScenarioAddedEventProto scenarioAddedEventProto = hubEventProto.getScenarioAdded();
        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEventProto.getName())
                .setActions(toDeviceActionAvro(scenarioAddedEventProto.getActionList()))
                .setConditions(toScenarioConditionAvro(scenarioAddedEventProto.getConditionList()))
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(hubEventProto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(hubEventProto.getTimestamp().getSeconds(), hubEventProto.getTimestamp().getNanos()))
                .setPayload(scenarioAddedEventAvro)
                .build();
    }

    private static List<DeviceActionAvro> toDeviceActionAvro(List<DeviceActionProto> deviceActionProtos) {

        List<DeviceActionAvro> deviceActionAvros = new ArrayList<>();

        for (DeviceActionProto deviceActionProto : deviceActionProtos) {
            deviceActionAvros.add(DeviceActionAvro.newBuilder()
                    .setValue(deviceActionProto.getValue())
                    .setSensorId(deviceActionProto.getSensorId())
                    .setType(ActionTypeAvro.valueOf(deviceActionProto.getType().name()))
                    .build());
        }

        return deviceActionAvros;
    }

    private static List<ScenarioConditionAvro> toScenarioConditionAvro(List<ScenarioConditionProto> scenarioConditionProtos) {

        List<ScenarioConditionAvro> scenarioConditionAvros = new ArrayList<>();
        Object value = null;

        for (ScenarioConditionProto scenarioConditionProto : scenarioConditionProtos) {

            if (scenarioConditionProto.getValueCase() == ScenarioConditionProto.ValueCase.INT_VALUE) {
                value = scenarioConditionProto.getIntValue();
            } else if (scenarioConditionProto.getValueCase() == ScenarioConditionProto.ValueCase.BOOL_VALUE) {
                value = scenarioConditionProto.getBoolValue();
            }

            scenarioConditionAvros.add(ScenarioConditionAvro.newBuilder()
                    .setValue(value)
                    .setType(ConditionTypeAvro.valueOf(scenarioConditionProto.getType().name()))
                    .setOperation(ConditionOperationAvro.valueOf(scenarioConditionProto.getOperation().name()))
                    .setSensorId(scenarioConditionProto.getSensorId())
                    .build());
        }

        return scenarioConditionAvros;
    }

}
