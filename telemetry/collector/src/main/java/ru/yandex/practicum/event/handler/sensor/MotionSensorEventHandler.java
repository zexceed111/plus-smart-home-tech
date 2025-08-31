package ru.yandex.practicum.event.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final EventService eventService;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        eventService.sendSensorEvent(mapFromProtoToAvro(event));
    }

    private SensorEventAvro mapFromProtoToAvro(SensorEventProto sensorEventProto) {
        MotionSensorProto motionSensorProto = sensorEventProto.getMotionSensorEvent();
        MotionSensorAvro motionSensorAvro = MotionSensorAvro.newBuilder()
                .setMotion(motionSensorProto.getMotion())
                .setVoltage(motionSensorProto.getVoltage())
                .setLinkQuality(motionSensorProto.getLinkQuality())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(sensorEventProto.getId())
                .setHubId(sensorEventProto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEventProto.getTimestamp().getSeconds(), sensorEventProto.getTimestamp().getNanos()))
                .setPayload(motionSensorAvro)
                .build();
    }
}
