package ru.yandex.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.event.model.sensor_event.SensorEvent;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class EventController {

    private final EventService service;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Поступил запрос Post /events/sensors с телом = {}", event);
        service.sendSensorEvent(event);
        log.info("Выполнен запрос Post /events/sensors с телом = {}", event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Поступил запрос Post /events/hubs с телом = {}", event);
        service.sendHubEvent(event);
        log.info("Выполнен запрос Post /events/hubs с телом = {}", event);
    }
}
