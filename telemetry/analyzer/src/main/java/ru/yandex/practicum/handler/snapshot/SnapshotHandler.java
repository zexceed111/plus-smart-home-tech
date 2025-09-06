package ru.yandex.practicum.handler.snapshot;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SnapshotHandler {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    private final ScenarioRepository scenarioRepository;
    private final Map<String, SensorEventHandler> sensorEventHandlers;

    public SnapshotHandler(ScenarioRepository scenarioRepository,
                           Set<SensorEventHandler> sensorEventHandlers) {
        this.scenarioRepository = scenarioRepository;
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getSensorType,
                        Function.identity()
                ));
    }

    public void handle(SensorsSnapshotAvro snapshotAvro) {

        log.info("На обработку поступил snapshot: {}", snapshotAvro);

        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshotAvro.getHubId());
        Map<String, SensorStateAvro> sensorStates = snapshotAvro.getSensorsState();

        scenarios = scenarios.stream()
                .filter(scenario -> checkConditions(scenario.getConditions(), sensorStates))
                .toList();

        for (Scenario scenario : scenarios) {

            List<Action> actions = scenario.getActions();
            for (Action action : actions) {
                DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensor().getId())
                        .setType(ActionTypeProto.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build();

                Timestamp timestamp = Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build();

                DeviceActionRequest request = DeviceActionRequest.newBuilder()
                        .setHubId(scenario.getHubId())
                        .setScenarioName(scenario.getName())
                        .setTimestamp(timestamp)
                        .setAction(actionProto)
                        .build();

                hubRouterClient.handleDeviceAction(request);
                log.info("Отправлено действие: {}", request);
            }

        }
    }

    private boolean checkConditions(List<Condition> conditions, Map<String, SensorStateAvro> sensorStates) {

        if (conditions == null || conditions.isEmpty()) {
            log.info("No conditions to check");
            return true;
        }

        if (sensorStates == null || sensorStates.isEmpty()) {
            log.warn("Sensor states are null or empty");
            return false;
        }

        return conditions.stream()
                .allMatch(condition -> checkCondition(condition, sensorStates.get(condition.getSensor().getId())));

    }

    private boolean checkCondition(Condition condition, SensorStateAvro sensorStateAvro) {

        if (condition == null) {
            log.warn("Condition is null");
            return false;
        }

        if (sensorStateAvro == null) {
            log.warn("SensorStateAvro is null for condition: {}", condition);
            return false;
        }

        if (sensorStateAvro.getData() == null) {
            log.warn("Sensor data is null for condition: {}", condition);
            return false;
        }

        String type = sensorStateAvro.getData().getClass().getName();
        if (!sensorEventHandlers.containsKey(type)) {
            throw new IllegalArgumentException("Не найден обработчик для сенсора: " + type);
        }

        Integer value = sensorEventHandlers.get(type).getSensorValue(condition.getType(), sensorStateAvro);

        if (value == null) {
            log.warn("Sensor value is null for condition: {}", condition);
            return false;
        }

        return switch (condition.getOperation()) {
            case ConditionOperation.LOWER_THAN -> value < condition.getValue();
            case ConditionOperation.EQUALS -> value.equals(condition.getValue());
            case ConditionOperation.GREATER_THAN -> value > condition.getValue();
        };

    }
}
