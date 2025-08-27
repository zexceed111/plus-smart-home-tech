package ru.yandex.practicum.event.kafka_client;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

public interface KafkaClient {

    Producer<String, SpecificRecordBase> getProducer();

    void stop();

}
