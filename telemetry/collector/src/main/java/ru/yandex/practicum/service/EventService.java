package ru.yandex.practicum.service;

import ru.yandex.practicum.model.hubs.HubEvent;
import ru.yandex.practicum.model.sensors.SensorEvent;

public interface EventService {

    void collectSensorEvent(SensorEvent sensorEvent);

    void collectHubEvent(HubEvent hubEvent);
}
