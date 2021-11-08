package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Content;
import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.From;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class PostNewMessageToConversationCommand implements UseCaseCommand<ConversationIdentifier> {

    private final From from;
    private final ConversationIdentifier conversationIdentifier;
    private final Content content;

    public PostNewMessageToConversationCommand(final From from,
                                               final ConversationIdentifier conversationIdentifier,
                                               final Content content) {
        this.from = Objects.requireNonNull(from);
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.content = Objects.requireNonNull(content);
    }

    @Override
    public ConversationIdentifier identifier() {
        return conversationIdentifier;
    }

    public From from() {
        return from;
    }

    public ConversationIdentifier conversationIdentifier() {
        return conversationIdentifier;
    }

    public Content content() {
        return content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PostNewMessageToConversationCommand)) return false;
        final PostNewMessageToConversationCommand that = (PostNewMessageToConversationCommand) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(conversationIdentifier, that.conversationIdentifier) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, conversationIdentifier, content);
    }
}
