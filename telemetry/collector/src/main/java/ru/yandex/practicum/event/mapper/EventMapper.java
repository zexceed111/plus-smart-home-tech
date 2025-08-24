package ru.yandex.practicum.event.mapper;

import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.hub_event.device.*;
import ru.yandex.practicum.event.model.hub_event.scenario.*;
import ru.yandex.practicum.event.model.sensor_event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    public static SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {

        Object payload;

        switch (sensorEvent) {
            case MotionSensorEvent event -> {
                payload = MotionSensorAvro.newBuilder()
                        .setLinkQuality(event.getLinkQuality())
                        .setMotion(event.isMotion())
                        .setVoltage(event.getVoltage())
                        .build();
            }
            case ClimateSensorEvent event -> {
                payload = ClimateSensorAvro.newBuilder()
                        .setCo2Level(event.getCo2Level())
                        .setHumidity(event.getHumidity())
                        .setTemperatureC(event.getTemperatureC())
                        .build();
            }
            case LightSensorEvent event -> {
                payload = LightSensorAvro.newBuilder()
                        .setLinkQuality(event.getLinkQuality())
                        .setLuminosity(event.getLuminosity())
                        .build();
            }
            case SwitchSensorEvent event -> {
                payload = SwitchSensorAvro.newBuilder()
                        .setState(event.isState())
                        .build();
            }
            case TemperatureSensorEvent event -> {
                payload = TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(event.getTemperatureC())
                        .setTemperatureF(event.getTemperatureF())
                        .build();
            }
            default -> {
                throw new RuntimeException("Ошибка конвертации события датчика: " + sensorEvent);
            }
        }

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public static HubEventAvro toHubEventAvro(HubEvent hubEvent) {

        Object payload;

        switch (hubEvent) {
            case DeviceAddedEvent event -> {
                payload = DeviceAddedEventAvro.newBuilder()
                        .setId(event.getId())
                        .setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
                        .build();
            }
            case DeviceRemovedEvent event -> {
                payload = DeviceRemovedEventAvro.newBuilder()
                        .setId(event.getId())
                        .build();
            }
            case ScenarioAddedEvent event -> {
                payload = ScenarioAddedEventAvro.newBuilder()
                        .setName(event.getName())
                        .setActions(toDeviceActionAvro(event.getActions()))
                        .setConditions(toScenarioConditionAvro(event.getConditions()))
                        .build();
            }
            case ScenarioRemovedEvent event -> {
                payload = ScenarioRemovedEventAvro.newBuilder()
                        .setName(event.getName())
                        .build();
            }
            default -> {
                throw new RuntimeException("Ошибка конвертации события хаба: " + hubEvent);
            }
        }

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private static List<DeviceActionAvro> toDeviceActionAvro(List<DeviceAction> deviceActions) {

        List<DeviceActionAvro> deviceActionAvros = new ArrayList<>();

        for (DeviceAction deviceAction : deviceActions) {
            deviceActionAvros.add(DeviceActionAvro.newBuilder()
                    .setValue(deviceAction.getValue())
                    .setSensorId(deviceAction.getSensorId())
                    .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                    .build());
        }

        return deviceActionAvros;
    }

    private static List<ScenarioConditionAvro> toScenarioConditionAvro(List<ScenarioCondition> scenarioConditions) {

        List<ScenarioConditionAvro> scenarioConditionAvros = new ArrayList<>();

        for (ScenarioCondition scenarioCondition : scenarioConditions) {
            scenarioConditionAvros.add(ScenarioConditionAvro.newBuilder()
                    .setValue(scenarioCondition.getValue())
                    .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                    .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()))
                    .setSensorId(scenarioCondition.getSensorId())
                    .build());
        }

        return scenarioConditionAvros;
    }

}
