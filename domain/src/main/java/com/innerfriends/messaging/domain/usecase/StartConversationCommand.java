package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;

import java.util.Objects;

public final class StartConversationCommand implements StartConversation, UseCaseCommand<Void> {

    private final From from;
    private final To to;// could be a list in case of a group
    private final StartedAt startedAt;
    private final Content content;

    public StartConversationCommand(final From from, final To to, final StartedAt startedAt, final Content content) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.startedAt = Objects.requireNonNull(startedAt);
        this.content = Objects.requireNonNull(content);
    }

    @Override
    public Void identifier() {
        return null;
    }

    @Override
    public From from() {
        return from;
    }

    @Override
    public To to() {
        return to;
    }

    @Override
    public StartedAt startAt() {
        return startedAt;
    }

    @Override
    public Content content() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StartConversationCommand)) return false;
        StartConversationCommand that = (StartConversationCommand) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(startedAt, that.startedAt) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, startedAt, content);
    }
}
