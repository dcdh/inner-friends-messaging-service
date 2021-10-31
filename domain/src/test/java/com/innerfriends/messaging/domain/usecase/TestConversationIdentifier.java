package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ConversationIdentifier;

import java.util.Objects;

public class TestConversationIdentifier implements ConversationIdentifier {

    private final String identifier;

    public TestConversationIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestConversationIdentifier)) return false;
        TestConversationIdentifier that = (TestConversationIdentifier) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
