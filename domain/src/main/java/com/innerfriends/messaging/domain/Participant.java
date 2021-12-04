package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class Participant {

    private final ParticipantIdentifier participantIdentifier;
    private final AddedAt addedAt;

    public Participant(final ConversationEvent conversationEvent) {
        this(conversationEvent.eventFrom().identifier(), new AddedAt(conversationEvent.eventAt()));
        if (!ConversationEventType.PARTICIPANT_ADDED.equals(conversationEvent.conversationEventType())) {
            throw new IllegalStateException("Not expected event !");
        }
    }

    public Participant(final ParticipantIdentifier participantIdentifier,
                       final AddedAt addedAt) {
        this.participantIdentifier = Objects.requireNonNull(participantIdentifier);
        this.addedAt = Objects.requireNonNull(addedAt);
    }

    public ParticipantIdentifier participantIdentifier() {
        return participantIdentifier;
    }

    public AddedAt addedAt() {
        return addedAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        final Participant that = (Participant) o;
        return Objects.equals(participantIdentifier, that.participantIdentifier) &&
                Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantIdentifier, addedAt);
    }
}
