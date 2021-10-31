package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class ListMessagesInConversationCommand implements UseCaseCommand<ConversationIdentifier> {

    private final ConversationIdentifier conversationIdentifier;

    public ListMessagesInConversationCommand(final ConversationIdentifier conversationIdentifier) {
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
        if (!(o instanceof ListMessagesInConversationCommand)) return false;
        ListMessagesInConversationCommand that = (ListMessagesInConversationCommand) o;
        return Objects.equals(conversationIdentifier, that.conversationIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationIdentifier);
    }
}
