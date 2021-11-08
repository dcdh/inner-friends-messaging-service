package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class ConversationIdentifier {

    private final String identifier;

    public ConversationIdentifier(final String identifier) {
        this.identifier = Objects.requireNonNull(identifier);
    }

    public String identifier() {
        return identifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationIdentifier)) return false;
        final ConversationIdentifier that = (ConversationIdentifier) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "ConversationIdentifier{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
