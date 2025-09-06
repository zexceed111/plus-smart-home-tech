package ru.yandex.practicum.kafka_client;

import org.apache.kafka.clients.consumer.Consumer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface KafkaClient {

    Consumer<String, SensorsSnapshotAvro> getSnapshotConsumer();

    Consumer<String, HubEventAvro> getHubEventConsumer();

    void stop();

}
