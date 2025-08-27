package ru.yandex.practicum.event.model.hub_event.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.yandex.practicum.event.model.hub_event.HubEvent;
import ru.yandex.practicum.event.model.hub_event.HubEventType;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {

    @NotBlank
    @Size(min = 3)
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
