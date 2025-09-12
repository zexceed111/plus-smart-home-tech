package ru.yandex.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.ConditionType;

@Slf4j
@Service
public class TemperatureSensorEventHandler implements SensorEventHandler {

    @Override
    public String getSensorType() {
        return TemperatureSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType conditionType, SensorStateAvro sensorState) {
        TemperatureSensorAvro temperatureSensor = (TemperatureSensorAvro) sensorState.getData();
        return switch (conditionType) {
            case TEMPERATURE -> temperatureSensor.getTemperatureC();
            default -> null;
        };
    }
}