package ru.yandex.practicum.kafka_client;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaClientImpl {

    private final KafkaProperties kafkaProperties;

    @Bean
    KafkaClient getClient() {
        return new KafkaClient() {

            private Consumer<String, SensorsSnapshotAvro> snapshotConsumer;
            private Consumer<String, HubEventAvro> hubEventConsumer;

            @Override
            public Consumer<String, SensorsSnapshotAvro> getSnapshotConsumer() {
                if (snapshotConsumer == null) {
                    initSnapshotConsumer();
                }
                return snapshotConsumer;
            }

            @Override
            public Consumer<String, HubEventAvro> getHubEventConsumer() {
                if (hubEventConsumer == null) {
                    initHubEventConsumer();
                }
                return hubEventConsumer;
            }

            @Override
            public void stop() {
                if (snapshotConsumer != null) {
                    snapshotConsumer.commitSync();
                    snapshotConsumer.close(Duration.ofSeconds(kafkaProperties.getCloseClientTimeoutSec()));
                }
                if (hubEventConsumer != null) {
                    hubEventConsumer.commitSync();
                    hubEventConsumer.close(Duration.ofSeconds(kafkaProperties.getCloseClientTimeoutSec()));
                }
            }

            private void initSnapshotConsumer() {
                Properties properties = new Properties();
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
                properties.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getSnapshotConsumer().getClientId());
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getSnapshotConsumer().getGroupId());
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getSnapshotConsumer().getKeyDeserializer());
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getSnapshotConsumer().getValueDeserializer());
                snapshotConsumer = new KafkaConsumer<>(properties);
                snapshotConsumer.subscribe(kafkaProperties.getSnapshotConsumer().getTopics());
            }

            private void initHubEventConsumer() {
                Properties properties = new Properties();
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
                properties.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getHubConsumer().getClientId());
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getHubConsumer().getGroupId());
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getHubConsumer().getKeyDeserializer());
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getHubConsumer().getValueDeserializer());
                hubEventConsumer = new KafkaConsumer<>(properties);
                hubEventConsumer.subscribe(kafkaProperties.getHubConsumer().getTopics());
            }

        };
    }
}