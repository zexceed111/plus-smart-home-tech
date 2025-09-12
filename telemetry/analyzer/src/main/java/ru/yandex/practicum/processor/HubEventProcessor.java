package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka_client.KafkaClient;
import ru.yandex.practicum.kafka_client.KafkaProperties;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HubEventProcessor implements Runnable {

    private final KafkaClient kafkaClient;
    private final KafkaProperties kafkaProperties;
    private final OffsetManager<HubEventAvro> offsetManager;
    private final Map<String, HubEventHandler> hubEventHandlers;

    public HubEventProcessor(KafkaClient kafkaClient,
                             KafkaProperties kafkaProperties,
                             OffsetManager<HubEventAvro> offsetManager,
                             Set<HubEventHandler> hubEventHandlers) {
        this.kafkaClient = kafkaClient;
        this.kafkaProperties = kafkaProperties;
        this.offsetManager = offsetManager;
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getEventType,
                        Function.identity()
                ));
        log.info("Создана база обработчиков: {}", hubEventHandlers);
    }

    @Override
    public void run() {
        log.info("Запущен HubEventProcessor");
        try (Consumer<String, HubEventAvro> hubEventConsumer = kafkaClient.getHubEventConsumer()) {
            while (true) {
                Long pollTimeout = kafkaProperties.getHubConsumer().getPollTimeoutSec();
                ConsumerRecords<String, HubEventAvro> records = hubEventConsumer.poll(Duration.ofMillis(pollTimeout));

                int count = 0;
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    handleRecord(record.value());
                    offsetManager.manageOffsets(record, count, kafkaProperties.getHubConsumer().getAutoCommitRecordCount(), hubEventConsumer);
                    count++;
                }
                hubEventConsumer.commitAsync();
            }
        } catch (Exception e) {
            log.error("Ошибка во время обработки HubEventAvro в analyzer", e);
        }
    }

    private void handleRecord(HubEventAvro hubEventAvro) {
        log.info("Обработка события: {}", hubEventAvro);
        String eventType = hubEventAvro.getPayload().getClass().getName();
        if (hubEventHandlers.containsKey(eventType)) {
            log.info("Используемый обработчик: {}", hubEventHandlers.get(eventType));
            hubEventHandlers.get(eventType).handle(hubEventAvro);
        } else {
            throw new IllegalArgumentException("Не найден обработчик для события: " + eventType);
        }
    }
}
