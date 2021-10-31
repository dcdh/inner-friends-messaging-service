package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;

import java.util.Objects;

public final class PostResponseToConversationCommand implements UseCaseCommand<ConversationIdentifier> {

    private final From from;
    private final ConversationIdentifier conversationIdentifier;
    private final Content content;
    private final PostedAt postedAt;

    public PostResponseToConversationCommand(final From from,
                                             final ConversationIdentifier conversationIdentifier,
                                             final Content content,
                                             final PostedAt postedAt) {
        this.from = Objects.requireNonNull(from);
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.content = Objects.requireNonNull(content);
        this.postedAt = Objects.requireNonNull(postedAt);
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

    public PostedAt postedAt() {
        return postedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostResponseToConversationCommand)) return false;
        PostResponseToConversationCommand that = (PostResponseToConversationCommand) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(conversationIdentifier, that.conversationIdentifier) &&
                Objects.equals(content, that.content) &&
                Objects.equals(postedAt, that.postedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, conversationIdentifier, content, postedAt);
    }
}
