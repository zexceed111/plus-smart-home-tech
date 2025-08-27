package ru.yandex.practicum.event.model.hub_event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.event.model.hub_event.device.DeviceAddedEvent;
import ru.yandex.practicum.event.model.hub_event.device.DeviceRemovedEvent;
import ru.yandex.practicum.event.model.hub_event.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.event.model.hub_event.scenario.ScenarioRemovedEvent;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = HubEventType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@Data
public abstract class HubEvent {

    @NotBlank
    private String hubId;
    private Instant timestamp = Instant.now();

    public abstract HubEventType getType();
}
