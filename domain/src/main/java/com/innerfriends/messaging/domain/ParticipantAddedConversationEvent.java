package com.innerfriends.messaging.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ParticipantAddedConversationEvent implements ConversationEvent {

    private final ParticipantIdentifier participantIdentifier;
    private final AddedAt addedAt;

    public ParticipantAddedConversationEvent(final ParticipantIdentifier participantIdentifier, final AddedAt addedAt) {
        this.participantIdentifier = Objects.requireNonNull(participantIdentifier);
        this.addedAt = Objects.requireNonNull(addedAt);
    }

    @Override
    public ConversationEventType conversationEventType() {
        return ConversationEventType.PARTICIPANT_ADDED;
    }

    @Override
    public EventFrom eventFrom() {
        return new EventFrom(participantIdentifier);
    }

    @Override
    public EventAt eventAt() {
        return new EventAt(addedAt);
    }

    @Override
    public Content content() {
        return new Content("");
    }

    @Override
    public List<ParticipantIdentifier> participantsIdentifier() {
        return Collections.emptyList();
    }

    @Override
    public Message toMessage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipantAddedConversationEvent)) return false;
        final ParticipantAddedConversationEvent that = (ParticipantAddedConversationEvent) o;
        return Objects.equals(participantIdentifier, that.participantIdentifier) &&
                Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantIdentifier, addedAt);
    }

    @Override
    public String toString() {
        return "ParticipantAddedConversationEvent{" +
                "participantIdentifier=" + participantIdentifier +
                ", addedAt=" + addedAt +
                '}';
    }
}
