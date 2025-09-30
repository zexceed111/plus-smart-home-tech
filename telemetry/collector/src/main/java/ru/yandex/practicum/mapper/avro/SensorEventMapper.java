package ru.yandex.practicum.mapper.avro;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.sensors.*;


public class SensorEventMapper {

    public static SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(toSensorEventPayloadAvro(sensorEvent))
                .build();
    }

    public static SpecificRecordBase toSensorEventPayloadAvro(SensorEvent sensorEvent) {
        switch (sensorEvent.getType()) {
            case MOTION_SENSOR -> {
                MotionSensorEvent event = (MotionSensorEvent) sensorEvent;
                return MotionSensorAvro.newBuilder()
                        .setLinkQuality(event.getLinkQuality())
                        .setMotion(event.isMotion())
                        .setVoltage(event.getVoltage())
                        .build();
            }

            case CLIMATE_SENSOR -> {
                ClimateSensorEvent event = (ClimateSensorEvent) sensorEvent;
                return ClimateSensorAvro.newBuilder()
                        .setTemperatureC(event.getTemperatureC())
                        .setHumidity(event.getHumidity())
                        .setCo2Level(event.getCo2Level())
                        .build();
            }

            case LIGHT_SENSOR -> {
                LightSensorEvent event = (LightSensorEvent) sensorEvent;
                return LightSensorAvro.newBuilder()
                        .setLinkQuality(event.getLinkQuality())
                        .setLuminosity(event.getLuminosity())
                        .build();
            }

            case SWITCH_SENSOR -> {
                SwitchSensorEvent event = (SwitchSensorEvent) sensorEvent;
                return SwitchSensorAvro.newBuilder()
                        .setState(event.isState())
                        .build();
            }

            case TEMPERATURE_SENSOR -> {
                TemperatureSensorEvent event = (TemperatureSensorEvent) sensorEvent;
                return TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(event.getTemperatureC())
                        .setTemperatureF(event.getTemperatureF())
                        .build();
            }

            default -> throw new IllegalStateException("Invalid payload: " + sensorEvent.getType());
        }
    }
}