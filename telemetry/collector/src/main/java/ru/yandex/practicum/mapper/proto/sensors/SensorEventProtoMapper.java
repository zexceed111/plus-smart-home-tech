package ru.yandex.practicum.mapper.proto.sensors;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensors.SensorEvent;

public interface SensorEventProtoMapper {
    SensorEventProto.PayloadCase getMessageType();

    SensorEvent map(SensorEventProto event);
}

