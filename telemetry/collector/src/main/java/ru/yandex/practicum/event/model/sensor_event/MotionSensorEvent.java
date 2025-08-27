package ru.yandex.practicum.event.model.sensor_event;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {

    @PositiveOrZero
    private int linkQuality;

    private boolean motion;

    @PositiveOrZero
    private int voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
