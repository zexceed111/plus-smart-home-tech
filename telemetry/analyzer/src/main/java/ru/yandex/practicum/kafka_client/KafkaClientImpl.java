package ru.yandex.practicum.kafka_client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaClientImpl {

    private final KafkaProperties kafkaProperties;

    @Bean
    KafkaClient getClient() {
        return new KafkaClient() {

            private Consumer<String, SensorsSnapshotAvro> snapshotConsumer;
            private Consumer<String, HubEventAvro> hubEventConsumer;

            private final ExecutorService executor = Executors.newFixedThreadPool(2);
            private final AtomicBoolean running = new AtomicBoolean(true);

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
                running.set(false);
                if (snapshotConsumer != null) {
                    snapshotConsumer.wakeup();
                }
                if (hubEventConsumer != null) {
                    hubEventConsumer.wakeup();
                }
                executor.shutdown();
            }

            private void initSnapshotConsumer() {
                snapshotConsumer = new KafkaConsumer<>(buildPropertiesForSnapshot());
                snapshotConsumer.subscribe(kafkaProperties.getSnapshotConsumer().getTopics());

                executor.submit(() -> runConsumerLoop(snapshotConsumer, "SnapshotConsumer"));
            }

            private void initHubEventConsumer() {
                hubEventConsumer = new KafkaConsumer<>(buildPropertiesForHub());
                hubEventConsumer.subscribe(kafkaProperties.getHubConsumer().getTopics());

                executor.submit(() -> runConsumerLoop(hubEventConsumer, "HubEventConsumer"));
            }

            private void runConsumerLoop(Consumer<?, ?> consumer, String name) {
                try {
                    while (running.get()) {
                        ConsumerRecords<?, ?> records = consumer.poll(Duration.ofMillis(500));
                        log.debug("{} polled {} records", name, records.count());
                    }
                } catch (WakeupException e) {
                    if (running.get()) {
                        throw e;
                    }
                    log.info("{} shutting down gracefully", name);
                } finally {
                    try {
                        consumer.commitSync();
                    } catch (Exception ex) {
                        log.warn("{} commitSync failed: {}", name, ex.getMessage());
                    } finally {
                        consumer.close(Duration.ofSeconds(kafkaProperties.getCloseClientTimeoutSec()));
                        log.info("{} closed", name);
                    }
                }
            }

            private Properties buildPropertiesForSnapshot() {
                Properties properties = new Properties();
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
                properties.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getSnapshotConsumer().getClientId());
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getSnapshotConsumer().getGroupId());
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getSnapshotConsumer().getKeyDeserializer());
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getSnapshotConsumer().getValueDeserializer());
                return properties;
            }

            private Properties buildPropertiesForHub() {
                Properties properties = new Properties();
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
                properties.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.getHubConsumer().getClientId());
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getHubConsumer().getGroupId());
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getHubConsumer().getKeyDeserializer());
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getHubConsumer().getValueDeserializer());
                return properties;
            }
        };
    }
}
