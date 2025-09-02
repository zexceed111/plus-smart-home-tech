package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;

    @Override
    public String getEventType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    @Transactional
    @Override
    public void handle(HubEventAvro event) {

        log.info("Поступил для сохранения scenario: {}", event);

        ScenarioAddedEventAvro scenarioEvent = (ScenarioAddedEventAvro) event.getPayload();
        validateSensors(scenarioEvent.getConditions(), scenarioEvent.getActions(), event.getHubId());

        Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(event.getHubId(), scenarioEvent.getName());
        Scenario scenario;

        List<Long> oldConditionIds = null;
        List<Long> oldActionIds = null;

        if (existingScenario.isEmpty()) {
            scenario = mapToScenario(event, scenarioEvent);
        } else {
            scenario = existingScenario.get();
            oldConditionIds = scenario.getConditions().stream().map(Condition::getId).toList();
            oldActionIds = scenario.getActions().stream().map(Action::getId).toList();

            scenario.setConditions(scenarioEvent.getConditions().stream()
                    .map(conditionAvro -> mapToCondition(scenario, conditionAvro))
                    .collect(Collectors.toList()));
            scenario.setActions(scenarioEvent.getActions().stream()
                    .map(actionAvro -> mapToAction(scenario, actionAvro))
                    .collect(Collectors.toList()));
        }

        scenarioRepository.save(scenario);
        log.info("В БД сохранен scenario: {}", scenario);

        cleanupUnusedConditions(oldConditionIds);
        cleanupUnusedActions(oldActionIds);
    }

    private void validateSensors(Collection<ScenarioConditionAvro> conditions, Collection<DeviceActionAvro> actions, String hubId) {
        List<String> conditionSensorIds = getConditionSensorIds(conditions);
        List<String> actionSensorIds = getActionSensorIds(actions);

        if (!sensorRepository.existsByIdInAndHubId(conditionSensorIds, hubId)) {
            throw new RuntimeException("Сенсоры для scenarioCondition не найдены");
        }
        if (!sensorRepository.existsByIdInAndHubId(actionSensorIds, hubId)) {
            throw new RuntimeException("Сенсоры для scenarioAction не найдены");
        }
    }

    private List<String> getConditionSensorIds(Collection<ScenarioConditionAvro> conditions) {
        return conditions.stream().map(ScenarioConditionAvro::getSensorId).collect(Collectors.toList());
    }

    private List<String> getActionSensorIds(Collection<DeviceActionAvro> actions) {
        return actions.stream().map(DeviceActionAvro::getSensorId).collect(Collectors.toList());
    }

    private void cleanupUnusedConditions(Collection<Long> conditionIds) {
        if (conditionIds != null && !conditionIds.isEmpty()) {
            conditionRepository.deleteAllById(conditionIds);
        }
    }

    private void cleanupUnusedActions(Collection<Long> actionIds) {
        if (actionIds != null && !actionIds.isEmpty()) {
            actionRepository.deleteAllById(actionIds);
        }
    }

    private Scenario mapToScenario(HubEventAvro hubEventAvro, ScenarioAddedEventAvro scenarioAddedEventAvro) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubEventAvro.getHubId());
        scenario.setName(scenarioAddedEventAvro.getName());
        scenario.setConditions(scenarioAddedEventAvro.getConditions().stream()
                .map(conditionAvro -> mapToCondition(scenario, conditionAvro))
                .toList());
        scenario.setActions(scenarioAddedEventAvro.getActions().stream()
                .map(actionAvro -> mapToAction(scenario, actionAvro))
                .toList());

        return scenario;
    }

    private Action mapToAction(Scenario scenario, DeviceActionAvro deviceActionAvro) {
        return Action.builder()
                .sensor(new Sensor(deviceActionAvro.getSensorId(), scenario.getHubId()))
                .type(toActionType(deviceActionAvro.getType()))
                .value(deviceActionAvro.getValue())
                .build();
    }

    private Condition mapToCondition(Scenario scenario, ScenarioConditionAvro conditionAvro) {
        return Condition.builder()
                .sensor(new Sensor(conditionAvro.getSensorId(), scenario.getHubId()))
                .type(toConditionType(conditionAvro.getType()))
                .operation(toConditionOperation(conditionAvro.getOperation()))
                .value(getConditionValue(conditionAvro.getValue()))
                .scenarios(List.of(scenario))
                .build();
    }

    private ActionType toActionType(ActionTypeAvro actionTypeAvro) {
        return ActionType.valueOf(actionTypeAvro.name());
    }

    private ConditionType toConditionType(ConditionTypeAvro conditionTypeAvro) {
        return ConditionType.valueOf(conditionTypeAvro.name());
    }

    private ConditionOperation toConditionOperation(ConditionOperationAvro conditionOperationAvro) {
        return ConditionOperation.valueOf(conditionOperationAvro.name());
    }

    private Integer getConditionValue(Object conditionValue) {
        if (conditionValue == null) {
            return null;
        }
        if (conditionValue instanceof Boolean) {
            return ((Boolean) conditionValue ? 1 : 0);
        }
        if (conditionValue instanceof Integer) {
            return (Integer) conditionValue;
        }
        throw new ClassCastException("Ошибка преобразования значения условия");
    }
}
