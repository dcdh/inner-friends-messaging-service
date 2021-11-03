package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;

import java.util.List;
import java.util.Objects;

public final class OpenANewConversationCommand implements OpenANewConversation, UseCaseCommand<Void> {

    private final OpenedBy openedBy;
    private final List<ParticipantIdentifier> participantsIdentifier;
    private final OpenedAt openedAt;
    private final Content content;

    public OpenANewConversationCommand(final OpenedBy openedBy,
                                       final List<ParticipantIdentifier> participantsIdentifier,
                                       final OpenedAt openedAt,
                                       final Content content) {
        this.openedBy = Objects.requireNonNull(openedBy);
        this.participantsIdentifier = Objects.requireNonNull(participantsIdentifier);
        this.openedAt = Objects.requireNonNull(openedAt);
        this.content = Objects.requireNonNull(content);
    }

    @Override
    public Void identifier() {
        return null;
    }

    @Override
    public OpenedBy openedBy() {
        return openedBy;
    }

    @Override
    public List<ParticipantIdentifier> participantsIdentifier() {
        return participantsIdentifier;
    }

    @Override
    public OpenedAt startedAt() {
        return openedAt;
    }

    @Override
    public Content content() {
        return content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenANewConversationCommand)) return false;
        final OpenANewConversationCommand that = (OpenANewConversationCommand) o;
        return Objects.equals(openedBy, that.openedBy) &&
                Objects.equals(participantsIdentifier, that.participantsIdentifier) &&
                Objects.equals(openedAt, that.openedAt) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openedBy, participantsIdentifier, openedAt, content);
    }
}
