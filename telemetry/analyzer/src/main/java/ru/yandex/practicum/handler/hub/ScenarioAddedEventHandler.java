package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.Mapper;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
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
    public void handle(HubEventAvro hubEvent) {
        ScenarioAddedEventAvro scenarioEvent = (ScenarioAddedEventAvro) hubEvent.getPayload();
        validateSensors(scenarioEvent.getConditions(), scenarioEvent.getActions(), hubEvent.getHubId());

        Optional<Scenario> existingScenario = scenarioRepository.findByHubIdAndName(hubEvent.getHubId(), scenarioEvent.getName());
        Scenario scenario;
        String logAction;
        List<Long> oldConditionIds = null;
        List<Long> oldActionIds = null;

        if (existingScenario.isEmpty()) {
            scenario = Mapper.mapToScenario(hubEvent, scenarioEvent);
            logAction = "added";
        } else {
            scenario = existingScenario.get();
            oldConditionIds = scenario.getConditions().stream().map(Condition::getId).collect(Collectors.toList());
            oldActionIds = scenario.getActions().stream().map(Action::getId).collect(Collectors.toList());

            scenario.setConditions(scenarioEvent.getConditions().stream()
                    .map(conditionAvro -> Mapper.mapToCondition(scenario, conditionAvro))
                    .collect(Collectors.toList()));
            scenario.setActions(scenarioEvent.getActions().stream()
                    .map(actionAvro -> Mapper.mapToAction(scenario, actionAvro))
                    .collect(Collectors.toList()));
            logAction = "updated";
        }

        scenarioRepository.save(scenario);
        log.info("Scenario {}: {}", logAction, scenario);
        cleanupUnusedConditions(oldConditionIds);
        cleanupUnusedActions(oldActionIds);
    }

    private void validateSensors(Collection<ScenarioConditionAvro> conditions, Collection<DeviceActionAvro> actions, String hubId) {
        List<String> conditionSensorIds = getConditionSensorIds(conditions);
        List<String> actionSensorIds = getActionSensorIds(actions);

        if (!sensorRepository.existsByIdInAndHubId(conditionSensorIds, hubId)) {
            throw new NotFoundException("Sensors for scenario conditions not found");
        }
        if (!sensorRepository.existsByIdInAndHubId(actionSensorIds, hubId)) {
            throw new NotFoundException("Sensors for scenario actions not found");
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
}