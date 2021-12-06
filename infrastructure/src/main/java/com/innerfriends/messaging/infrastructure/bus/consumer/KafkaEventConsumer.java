package com.innerfriends.messaging.infrastructure.bus.consumer;

import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.apache.kafka.common.header.Header;

import java.nio.charset.Charset;

public abstract class KafkaEventConsumer {

    public String getHeaderAsString(final KafkaRecord<?, ?> record, final String name) {
        final Header header = record.getHeaders().lastHeader(name);
        if (header == null) {
            throw new IllegalArgumentException("Expected record header '" + name + "' not present");
        }
        return new String(header.value(), Charset.forName("UTF-8"));
    }

}
