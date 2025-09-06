package ru.yandex.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.model.ConditionType;

@Slf4j
@Service
public class ClimateSensorEventHandler implements SensorEventHandler {

    @Override
    public String getSensorType() {
        return ClimateSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType conditionType, SensorStateAvro sensorState) {

        ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();

        return switch (conditionType) {
            case TEMPERATURE -> climateSensor.getTemperatureC();
            case CO2LEVEL -> climateSensor.getCo2Level();
            case HUMIDITY -> climateSensor.getHumidity();
            default -> null;
        };
    }
}