package ru.yandex.practicum.mapper.proto.hubs;

import ru.yandex.practicum.model.hubs.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventProtoMapper {
    HubEventProto.PayloadCase getMessageType();

    HubEvent map(HubEventProto event);
}
