package ru.yandex.practicum.collector.model.sensor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClimateSensorEvent extends SensorEvent {

    private int temperatureC;
    private int humidity;
    private int co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}

