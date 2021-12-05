package com.innerfriends.messaging.infrastructure.outbox.contactbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innerfriends.messaging.domain.Contact;
import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.infrastructure.InstantProvider;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class ContactAddedIntoContactBookEvent implements ContactBookExportedEvent {

    private final Owner owner;
    private final JsonNode addContactIntoContactBook;
    private final Instant timestamp;

    private ContactAddedIntoContactBookEvent(final Owner owner, final JsonNode addContactIntoContactBook, final Instant timestamp) {
        this.owner = Objects.requireNonNull(owner);
        this.addContactIntoContactBook = Objects.requireNonNull(addContactIntoContactBook);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    public static ContactAddedIntoContactBookEvent of(final ContactBook contactBookWithNewAddedContact,
                                                      final ObjectMapper objectMapper,
                                                      final InstantProvider instantProvider) {
        final Contact lastAddedContact = contactBookWithNewAddedContact.getLastAddedContact();
        final ObjectNode asJson = objectMapper.createObjectNode()
                .put("owner", contactBookWithNewAddedContact.owner().identifier().identifier())
                .put("version", contactBookWithNewAddedContact.version());
        final ObjectNode addedContact = asJson.putObject("addedContact");
        addedContact
                .put("contactIdentifier", lastAddedContact.contactIdentifier().identifier())
                .put("addedAt", lastAddedContact.addedAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        return new ContactAddedIntoContactBookEvent(contactBookWithNewAddedContact.owner(), asJson, instantProvider.now());
    }

    @Override
    public String getAggregateId() {
        return owner.identifier().identifier();
    }

    @Override
    public String getType() {
        return "ContactAddedIntoContactBook";
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
