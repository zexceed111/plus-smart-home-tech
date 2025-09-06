package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public String getEventType() {
        return DeviceRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro deviceRemovedEvent = (DeviceRemovedEventAvro) event.getPayload();

        if (sensorRepository.findByIdAndHubId(deviceRemovedEvent.getId(), event.getHubId()).isEmpty()) {
            throw new NotFoundException("Не найден сенсор с id = " + deviceRemovedEvent.getId() + " в пространстве hubId = " + event.getHubId());
        }
        sensorRepository.deleteById(deviceRemovedEvent.getId());
        log.info("Из БД удален sensor/device с id  = {}", deviceRemovedEvent.getId());
    }

}
