package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.hub.enums.ScenarioConditionOperation;
import ru.yandex.practicum.model.hub.enums.ScenarioConditionType;

@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {
    @NotBlank
    String sensorID;
    @NotNull
    ScenarioConditionType type;
    @NotNull
    ScenarioConditionOperation operation;

    int value;
}
