package com.innerfriends.messaging.infrastructure.outbox.conversation;

import com.fasterxml.jackson.databind.JsonNode;
import io.debezium.outbox.quarkus.ExportedEvent;

public interface ConversationExportedEvent extends ExportedEvent<String, JsonNode> {

    @Override
    default String getAggregateType() {
        return "Conversation";
    }

}
