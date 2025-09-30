package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KafkaConsumerSettings {
        private String groupId;
        private String clientId;
        private String keyDeserializer;
        private String valueDeserializer;
        private Boolean autoCommit;
        private long attemptTimeout;
}
