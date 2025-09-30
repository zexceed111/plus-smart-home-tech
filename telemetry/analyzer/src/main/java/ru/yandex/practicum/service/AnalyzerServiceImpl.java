package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyzerServiceImpl implements AnalyzerService {
    private final ScenarioRepository scenarioRepository;
    private final Map<String, SensorEventHandler> sensorEventHandlers;

    public AnalyzerServiceImpl(ScenarioRepository scenarioRepository,
                               Set<SensorEventHandler> sensorEventHandlers) {
        this.scenarioRepository = scenarioRepository;
        log.info("AnalyzerServiceImpl: scenarioRepository initialized");
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getSensorType,
                        Function.identity()
                ));
        log.info("AnalyzerServiceImpl: sensorEventHandlers initialized");
    }

    @Override
    public List<Scenario> getScenariosBySnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        if (sensorsSnapshotAvro == null) {
            log.warn("SensorsSnapshotAvro is null");
            return List.of();
        }

        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshotAvro.getHubId());
        Map<String, SensorStateAvro> sensorStates = sensorsSnapshotAvro.getSensorsState();
        log.info("scenarios in repository count {} ", scenarios.size());

        return scenarios.stream()
                .filter(scenario -> checkConditions(scenario.getConditions(), sensorStates))
                .toList();
    }

    private boolean checkConditions(List<Condition> conditions, Map<String, SensorStateAvro> sensorStates) {
        if (conditions == null || conditions.isEmpty()) {
            log.info("No conditions to check");
            return true; // Если условий нет, считаем, что они выполнены
        }

        if (sensorStates == null || sensorStates.isEmpty()) {
            log.warn("Sensor states are null or empty");
            return false; // Если нет данных сенсоров, условия не могут быть выполнены
        }

        log.info("<<<< checkConditions: conditions {}", conditions);
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
            log.error("No handler found for sensor type: {}", type);
            throw new IllegalArgumentException("Не могу найти обработчик для сенсора " + type);
        }

        Integer value = sensorEventHandlers.get(type).getSensorValue(condition.getType(), sensorStateAvro);
        log.info("check condition {} for sensor state {} ", condition, sensorStateAvro);

        if (value == null) {
            log.warn("Sensor value is null for condition: {}", condition);
            return false;
        }

        log.info("condition value = {}, sensor value = {}", condition.getValue(), value);
        return switch (condition.getOperation()) {
            case ConditionOperation.LOWER_THAN -> value < condition.getValue();
            case ConditionOperation.EQUALS -> value.equals(condition.getValue());
            case ConditionOperation.GREATER_THAN -> value > condition.getValue();
            default -> {
                log.warn("Unknown condition operation: {}", condition.getOperation());
                yield false;
            }
        };
    }
}