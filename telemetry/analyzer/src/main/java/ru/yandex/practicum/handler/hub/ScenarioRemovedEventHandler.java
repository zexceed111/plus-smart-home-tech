package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.repository.ScenarioRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;

    @Override
    public String getEventType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) event.getPayload();

        if (scenarioRepository.findByHubIdAndName(event.getHubId(), scenarioRemovedEvent.getName()).isEmpty()) {
            throw new NotFoundException("Не найден сценарий с name = " + scenarioRemovedEvent.getName() + " для hubId = " + event.getHubId());
        }

        scenarioRepository.deleteByNameAndHubId(scenarioRemovedEvent.getName(), event.getHubId());
        log.info("Из БД удален scenario с name  = {}", scenarioRemovedEvent.getName());
    }

}
