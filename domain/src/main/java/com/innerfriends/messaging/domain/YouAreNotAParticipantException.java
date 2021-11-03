package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class YouAreNotAParticipantException extends RuntimeException {

    private final ConversationIdentifier conversationIdentifier;
    private final From from;

    public YouAreNotAParticipantException(final ConversationIdentifier conversationIdentifier, final From from) {
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.from = Objects.requireNonNull(from);
    }

    public ConversationIdentifier getConversationIdentifier() {
        return conversationIdentifier;
    }

    public From getFrom() {
        return from;
    }
}
