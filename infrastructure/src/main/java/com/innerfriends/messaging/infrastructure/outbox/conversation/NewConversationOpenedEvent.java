package com.innerfriends.messaging.infrastructure.outbox.conversation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.ParticipantIdentifier;
import com.innerfriends.messaging.infrastructure.InstantProvider;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class NewConversationOpenedEvent implements ConversationExportedEvent {

    private final ConversationIdentifier conversationIdentifier;
    private final JsonNode startConversation;
    private final Instant timestamp;

    private NewConversationOpenedEvent(final ConversationIdentifier conversationIdentifier, final JsonNode startConversation, final Instant timestamp) {
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.startConversation = Objects.requireNonNull(startConversation);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    public static NewConversationOpenedEvent of(final Conversation conversation,
                                                final ObjectMapper objectMapper,
                                                final InstantProvider instantProvider) {
        final ObjectNode asJson = objectMapper.createObjectNode()
                .put("conversationIdentifier", conversation.conversationIdentifier().identifier())
                .put("version", conversation.version());
        final ObjectNode firstMessage = asJson.putObject("firstMessage");
        firstMessage.put("from", conversation.lastMessage().from().identifier().identifier())
                .put("content", conversation.lastMessage().content().content())
                .put("postedAt", conversation.lastMessage().postedAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        final ArrayNode participantsIdentifier = asJson.putArray("participantsIdentifier");
        conversation.participants()
                .stream()
                .map(ParticipantIdentifier::identifier)
                .forEach(participantIdentifier -> participantsIdentifier.add(participantIdentifier));
        return new NewConversationOpenedEvent(conversation.conversationIdentifier(), asJson, instantProvider.now());
    }

    @Override
    public String getAggregateId() {
        return conversationIdentifier.identifier();
    }

    @Override
    public String getType() {
        return "NewConversationOpened";
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonNode getPayload() {
        return startConversation;
    }
}
