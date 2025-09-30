package ru.yandex.practicum.model.hubs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {

    @NotBlank
    String sensorId;

    @NotNull
    ConditionType conditionType;

    @NotNull
    ConditionOperation conditionOperation;

    @NotNull
    Object value;
}