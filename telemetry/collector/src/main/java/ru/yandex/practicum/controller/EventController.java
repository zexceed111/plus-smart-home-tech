package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.mapper.proto.hubs.HubEventProtoMapper;
import ru.yandex.practicum.mapper.proto.sensors.SensorEventProtoMapper;
import ru.yandex.practicum.service.EventService;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final Map<SensorEventProto.PayloadCase, SensorEventProtoMapper> sensorEventMappers;
    private final Map<HubEventProto.PayloadCase, HubEventProtoMapper> hubEventMappers;
    private final EventService eventService;

    public EventController(Set<SensorEventProtoMapper> sensorEventMappers,
                           Set<HubEventProtoMapper> hubEventMappers,
                           EventService eventService) {
        this.sensorEventMappers = sensorEventMappers.stream()
                .collect(Collectors.toMap(
                        SensorEventProtoMapper::getMessageType,
                        Function.identity()
                ));
        this.hubEventMappers = hubEventMappers.stream()
                .collect(Collectors.toMap(
                        HubEventProtoMapper::getMessageType,
                        Function.identity()
                ));
        this.eventService = eventService;
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получен запрос: \n" + request.getAllFields());
            if (sensorEventMappers.containsKey(request.getPayloadCase())) {
                eventService.collectSensorEvent(sensorEventMappers.get(request.getPayloadCase()).map(request));
            } else {
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получен запрос: \n" + request.getAllFields());
            if (hubEventMappers.containsKey(request.getPayloadCase())) {
                eventService.collectHubEvent(hubEventMappers.get(request.getPayloadCase()).map(request));
            } else {
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
