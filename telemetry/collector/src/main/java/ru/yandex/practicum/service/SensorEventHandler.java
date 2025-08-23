package ru.yandex.practicum.service;

import ru.yandex.practicum.model.SensorEvent;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
