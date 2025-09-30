package ru.yandex.practicum.handler.sensor;

import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

public interface SensorEventHandler {

    /**
     * Возвращает тип сенсора, который обрабатывает этот обработчик.
     *
     * @return имя класса сенсора
     */
    String getSensorType();

    /**
     * Возвращает значение сенсора в зависимости от типа условия.
     *
     * @param conditionType тип условия (например, температура, влажность и т.д.)
     * @param sensorState состояние сенсора
     * @return значение сенсора или null, если тип условия не поддерживается
     */
    Integer getSensorValue(ConditionType conditionType, SensorStateAvro sensorState);
}