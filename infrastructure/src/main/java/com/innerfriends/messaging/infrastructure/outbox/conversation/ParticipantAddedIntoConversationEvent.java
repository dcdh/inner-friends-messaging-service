package com.innerfriends.messaging.infrastructure.outbox.conversation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.infrastructure.InstantProvider;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ParticipantAddedIntoConversationEvent implements ConversationExportedEvent {

    private final ConversationIdentifier conversationIdentifier;
    private final JsonNode participantAddedIntoConversation;
    private final Instant timestamp;

    private ParticipantAddedIntoConversationEvent(final ConversationIdentifier conversationIdentifier,
                                                  final JsonNode participantAddedIntoConversation,
                                                  final Instant timestamp) {
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.participantAddedIntoConversation = Objects.requireNonNull(participantAddedIntoConversation);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    public static ParticipantAddedIntoConversationEvent of(final Conversation conversation,
                                                           final ObjectMapper objectMapper,
                                                           final InstantProvider instantProvider) {
        final ObjectNode asJson = objectMapper.createObjectNode()
                .put("conversationIdentifier", conversation.conversationIdentifier().identifier())
                .put("participantAddedIntoConversation", conversation.lastAddedParticipant().participantIdentifier().identifier())
                .put("addedAt", conversation.lastAddedParticipant().addedAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .put("version", conversation.version());
        return new ParticipantAddedIntoConversationEvent(conversation.conversationIdentifier(), asJson, instantProvider.now());
    }

    @Override
    public String getAggregateId() {
        return conversationIdentifier.identifier();
    }

    @Override
    public String getType() {
        return "ParticipantAddedIntoConversation";
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonNode getPayload() {
        return participantAddedIntoConversation;
    }
}
