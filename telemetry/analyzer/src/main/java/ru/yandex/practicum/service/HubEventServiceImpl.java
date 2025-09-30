package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HubEventServiceImpl implements HubEventService {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    private final Map<String, HubEventHandler> hubEventHandlers;

    public HubEventServiceImpl(Set<HubEventHandler> hubEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getEventType,
                        Function.identity()
                ));
    }

    @Override
    public void process(HubEventAvro hubEvent) {
        log.info("Начало обработки события: {}", hubEvent);
        String eventType = hubEvent.getPayload().getClass().getName();
        if (hubEventHandlers.containsKey(eventType)) {
            log.info("Обработка события с помощью обработчика: {}", hubEventHandlers.get(eventType));
            hubEventHandlers.get(eventType).handle(hubEvent);
        } else {
            throw new IllegalArgumentException("Не удалось найти обработчик для события типа: " + eventType);
        }
    }

    @Override
    public void sendActionsByScenario(Scenario scenario) {
        log.info("Отправка сценария: {} с {} действиями", scenario.getName(), scenario.getActions().size());
        String hubId = scenario.getHubId();
        String scenarioName = scenario.getName();

        for (Action action : scenario.getActions()) {
            log.info("Actions: {}", action.getId());

            Instant timestamp = Instant.now();
            DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                    .setSensorId(action.getSensor().getId())
                    .setType(ActionTypeProto.valueOf(action.getType().name()));

            if (action.getType().equals(ActionType.SET_VALUE)){
                builder.setValue(action.getValue());
            }
            DeviceActionProto deviceAction = builder.build();

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenarioName)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(timestamp.getEpochSecond())
                            .setNanos(timestamp.getNano()))
                    .setAction(deviceAction)
                    .build();

            hubRouterClient.handleDeviceAction(request);
            log.info("Действие отправлено: {}", request);
        }
    }
}