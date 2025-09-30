package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aggregator.kafka.config")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConfigProperties {
    String bootstrapServers;

    String producerClientIdConfig;
    String producerKeySerializer;
    String producerValueSerializer;

    String consumerGroupId;
    String consumerClientIdConfig;
    String consumerKeyDeserializer;
    String consumerValueDeserializer;
    Boolean consumerAutoCommit;
    long consumeAttemptTimeout;

    String sensorEventsTopic;
    String sensorSnapshotsTopic;
}