package com.innerfriends.messaging.infrastructure.outbox.conversation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.Message;
import com.innerfriends.messaging.domain.ParticipantIdentifier;
import com.innerfriends.messaging.infrastructure.InstantProvider;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class NewMessagePostedToConversationEvent implements ConversationExportedEvent {

    private final ConversationIdentifier conversationIdentifier;
    private final JsonNode postNewMessageToConversation;
    private final Instant timestamp;

    private NewMessagePostedToConversationEvent(final ConversationIdentifier conversationIdentifier, final JsonNode postNewMessageToConversation, final Instant timestamp) {
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.postNewMessageToConversation = Objects.requireNonNull(postNewMessageToConversation);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    public static NewMessagePostedToConversationEvent of(final Conversation conversation,
                                                         final ObjectMapper objectMapper,
                                                         final InstantProvider instantProvider) {
        final Message messagePosted = conversation.lastMessage();
        final ObjectNode asJson = objectMapper.createObjectNode()
                .put("conversationIdentifier", conversation.conversationIdentifier().identifier())
                .put("version", conversation.version());
        final ObjectNode message = asJson.putObject("postedMessage");
        message
                .put("from", messagePosted.from().identifier().identifier())
                .put("content", messagePosted.content().content())
                .put("postedAt", messagePosted.postedAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        final ArrayNode participantsIdentifier = asJson.putArray("participantsIdentifier");
        conversation.participants()
                .stream()
                .map(ParticipantIdentifier::identifier)
                .forEach(participantIdentifier -> participantsIdentifier.add(participantIdentifier));
        return new NewMessagePostedToConversationEvent(conversation.conversationIdentifier(), asJson, instantProvider.now());
    }

    @Override
    public String getAggregateId() {
        return conversationIdentifier.identifier();
    }

    @Override
    public String getType() {
        return "NewMessagePostedToConversation";
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonNode getPayload() {
        return postNewMessageToConversation;
    }
}
