package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class ListConversationEventCommand implements UseCaseCommand<ConversationIdentifier> {

    private final ConversationIdentifier conversationIdentifier;

    public ListConversationEventCommand(final ConversationIdentifier conversationIdentifier) {
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
    }

    @Override
    public ConversationIdentifier identifier() {
        return conversationIdentifier;
    }

    public ConversationIdentifier conversationIdentifier() {
        return conversationIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListConversationEventCommand)) return false;
        ListConversationEventCommand that = (ListConversationEventCommand) o;
        return Objects.equals(conversationIdentifier, that.conversationIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationIdentifier);
    }
}
