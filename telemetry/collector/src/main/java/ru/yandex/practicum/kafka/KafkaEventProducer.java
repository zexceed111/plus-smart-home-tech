package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<Void, Object> kafkaTemplate;

    public <T> void send(String topic, T event) {
            kafkaTemplate.send(topic, event);
    }

}
