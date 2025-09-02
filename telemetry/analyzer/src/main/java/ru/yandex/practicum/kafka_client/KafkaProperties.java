package ru.yandex.practicum.kafka_client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.config")
public class KafkaProperties {

    private String bootstrapAddress;
    private Integer closeClientTimeoutSec;

    @NestedConfigurationProperty
    private KafkaConsumerProperties hubConsumer;

    @NestedConfigurationProperty
    private KafkaConsumerProperties snapshotConsumer;

}
