package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class UnknownConversationException extends RuntimeException {

    private final ConversationIdentifier unknownConversationIdentifier;

    public UnknownConversationException(final ConversationIdentifier unknownConversationIdentifier) {
        this.unknownConversationIdentifier = Objects.requireNonNull(unknownConversationIdentifier);
    }

    public ConversationIdentifier getUnknownConversationIdentifier() {
        return unknownConversationIdentifier;
    }
}
