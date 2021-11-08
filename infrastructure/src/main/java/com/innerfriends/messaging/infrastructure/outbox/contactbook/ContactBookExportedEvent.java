package com.innerfriends.messaging.infrastructure.outbox.contactbook;

import com.fasterxml.jackson.databind.JsonNode;
import io.debezium.outbox.quarkus.ExportedEvent;

public interface ContactBookExportedEvent extends ExportedEvent<String, JsonNode> {

    @Override
    default String getAggregateType() {
        return "ContactBook";
    }

}
