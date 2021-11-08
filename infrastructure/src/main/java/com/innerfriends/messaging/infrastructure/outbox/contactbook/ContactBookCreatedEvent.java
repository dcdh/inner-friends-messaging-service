package com.innerfriends.messaging.infrastructure.outbox.contactbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.infrastructure.InstantProvider;

import java.time.Instant;
import java.util.Objects;

public final class ContactBookCreatedEvent implements ContactBookExportedEvent {

    private final Owner owner;
    private final JsonNode addContactIntoContactBook;
    private final Instant timestamp;

    private ContactBookCreatedEvent(final Owner owner, final JsonNode addContactIntoContactBook, final Instant timestamp) {
        this.owner = Objects.requireNonNull(owner);
        this.addContactIntoContactBook = Objects.requireNonNull(addContactIntoContactBook);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    public static ContactBookCreatedEvent of(final ContactBook contactBookNewlyCreated,
                                             final ObjectMapper objectMapper,
                                             final InstantProvider instantProvider) {
        final ObjectNode asJson = objectMapper.createObjectNode()
                .put("owner", contactBookNewlyCreated.owner().identifier().identifier())
                .put("version", contactBookNewlyCreated.version());
        return new ContactBookCreatedEvent(contactBookNewlyCreated.owner(), asJson, instantProvider.now());
    }

    @Override
    public String getAggregateId() {
        return owner.identifier().identifier();
    }

    @Override
    public String getType() {
        return "ContactBookCreated";
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonNode getPayload() {
        return addContactIntoContactBook;
    }

}
