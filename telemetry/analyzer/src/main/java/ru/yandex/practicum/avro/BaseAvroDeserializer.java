package ru.yandex.practicum.avro;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.practicum.exception.DeserializationException;

@Slf4j
public abstract class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final DecoderFactory decoderFactory;
    private final DatumReader<T> datumReader;

    protected BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    protected BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.datumReader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            BinaryDecoder binaryDecoder = decoderFactory.binaryDecoder(data, null);
            return datumReader.read(null, binaryDecoder);
        } catch (Exception e) {
            log.error("Failed to deserialize data for topic: {}", topic, e);
            throw new DeserializationException("Failed to deserialize data", e);
        }
    }
}