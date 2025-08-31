package ru.yandex.practicum.event.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {

    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);

}
