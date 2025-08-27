package ru.yandex.practicum.event.model.hub_event.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.hub_event.HubEventType;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {

    @NotBlank
    String id;

    @NotNull
    DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
