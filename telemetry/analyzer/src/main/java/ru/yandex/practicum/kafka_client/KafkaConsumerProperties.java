package ru.yandex.practicum.kafka_client;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KafkaConsumerProperties {

    private String clientId;
    private String groupId;
    private List<String> topics;
    private String keyDeserializer;
    private String valueDeserializer;
    private Long pollTimeoutSec;

}
