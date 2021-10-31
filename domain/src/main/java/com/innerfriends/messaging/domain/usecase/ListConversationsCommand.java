package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ParticipantIdentifier;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class ListConversationsCommand implements UseCaseCommand<ParticipantIdentifier> {

    private final ParticipantIdentifier participantIdentifier;

    public ListConversationsCommand(final ParticipantIdentifier participantIdentifier) {
        this.participantIdentifier = Objects.requireNonNull(participantIdentifier);
    }

    public ParticipantIdentifier participantIdentifier() {
        return participantIdentifier;
    }

    @Override
    public ParticipantIdentifier identifier() {
        return participantIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListConversationsCommand)) return false;
        ListConversationsCommand that = (ListConversationsCommand) o;
        return Objects.equals(participantIdentifier, that.participantIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantIdentifier);
    }
}
