package ru.yandex.practicum.event.model.hub_event.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScenarioCondition {

    @NotBlank
    private String sensorId;

    @NotNull
    private ConditionType type;

    @NotNull
    private OperationType operation;

    int value;
}
