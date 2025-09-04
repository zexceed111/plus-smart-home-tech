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
                .collect(Collectors.toMap(SensorEventHandler::getSensorType, Function.identity()));
    }

    public void handle(SensorsSnapshotAvro snapshotAvro) {
        log.info("Обрабатываем snapshot: {}", snapshotAvro);

        // Загружаем сценарии вместе с conditions и actions
        List<Scenario> scenarios = scenarioRepository.findByHubIdWithConditionsAndActions(snapshotAvro.getHubId());
        if (scenarios.isEmpty()) {
            log.warn("Сценарии не найдены для хаба {}", snapshotAvro.getHubId());
            return;
        }

        Map<String, SensorStateAvro> sensorStates = snapshotAvro.getSensorsState();

        // Фильтруем сценарии по условиям
        List<Scenario> filteredScenarios = scenarios.stream()
                .filter(s -> checkConditions(s.getConditions(), sensorStates))
                .toList();

        if (filteredScenarios.isEmpty()) {
            log.info("Нет сценариев, удовлетворяющих условиям");
            return;
        }

        for (Scenario scenario : filteredScenarios) {
            log.info("Выполняем сценарий: {}", scenario.getName());
            for (Action action : scenario.getActions()) {
                try {
                    sendAction(scenario.getHubId(), scenario.getName(), action);
                } catch (Exception e) {
                    log.error("Ошибка при отправке действия {} для сценария {}", action, scenario.getName(), e);
                }
            }
        }
    }

    private void sendAction(String hubId, String scenarioName, Action action) {
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
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setTimestamp(timestamp)
                .setAction(actionProto)
                .build();

        log.info("Отправляем gRPC действие: {}", request);
        hubRouterClient.handleDeviceAction(request);
    }

    private boolean checkConditions(List<Condition> conditions, Map<String, SensorStateAvro> sensorStates) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        if (sensorStates == null || sensorStates.isEmpty()) {
            log.warn("Sensor states пустые");
            return false;
        }
        return conditions.stream()
                .allMatch(c -> checkCondition(c, sensorStates.get(c.getSensor().getId())));
    }

    private boolean checkCondition(Condition condition, SensorStateAvro sensorStateAvro) {
        if (condition == null || sensorStateAvro == null || sensorStateAvro.getData() == null) {
            log.warn("Невозможно проверить condition {}: данные отсутствуют", condition);
            return false;
        }

        String type = sensorStateAvro.getData().getClass().getName();
        SensorEventHandler handler = sensorEventHandlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("Не найден обработчик для сенсора: " + type);
        }

        Integer value = handler.getSensorValue(condition.getType(), sensorStateAvro);
        if (value == null) {
            log.warn("Значение сенсора null для condition {}", condition);
            return false;
        }

        return switch (condition.getOperation()) {
            case ConditionOperation.LOWER_THAN -> value < condition.getValue();
            case ConditionOperation.EQUALS -> value.equals(condition.getValue());
            case ConditionOperation.GREATER_THAN -> value > condition.getValue();
        };
    }
}
