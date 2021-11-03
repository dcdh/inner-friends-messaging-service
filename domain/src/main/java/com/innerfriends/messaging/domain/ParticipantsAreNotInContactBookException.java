package com.innerfriends.messaging.domain;

import java.util.List;
import java.util.Objects;

public final class ParticipantsAreNotInContactBookException extends RuntimeException {

    private final List<ParticipantIdentifier> participantIdentifiersNotInContactBook;

    public ParticipantsAreNotInContactBookException(final List<ParticipantIdentifier> participantIdentifiersNotInContactBook) {
        this.participantIdentifiersNotInContactBook = Objects.requireNonNull(participantIdentifiersNotInContactBook);
    }

    public List<ParticipantIdentifier> participantIdentifiersNotInContactBook() {
        return participantIdentifiersNotInContactBook;
    }

}
