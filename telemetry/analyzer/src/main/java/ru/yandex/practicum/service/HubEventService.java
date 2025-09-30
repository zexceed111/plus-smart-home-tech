package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventService {
    void process(HubEventAvro hubEventAvro);

    void sendActionsByScenario(Scenario scenario);
}
