package ru.yandex.practicum.service;

import ru.yandex.practicum.model.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();

    void handle(HubEvent event);
}
