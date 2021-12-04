package com.innerfriends.messaging.domain;

import java.util.List;
import java.util.Objects;

public final class StartedConversationEvent implements ConversationEvent {

    private final Message message;
    private final List<ParticipantIdentifier> participantsIdentifier;

    public StartedConversationEvent(final Message message, final List<ParticipantIdentifier> participantsIdentifier) {
        this.message = Objects.requireNonNull(message);
        this.participantsIdentifier = Objects.requireNonNull(participantsIdentifier);
        if (!participantsIdentifier.contains(this.message.from().identifier())) {
            throw new IllegalStateException("from must be in participants");
        }
    }

    @Override
    public ConversationEventType conversationEventType() {
        return ConversationEventType.STARTED;
    }

    @Override
    public EventFrom eventFrom() {
        return new EventFrom(message.from());
    }

    @Override
    public EventAt eventAt() {
        return new EventAt(message.postedAt());
    }

    @Override
    public Content content() {
        return message.content();
    }

    @Override
    public List<ParticipantIdentifier> participantsIdentifier() {
        return participantsIdentifier;
    }

    @Override
    public Message toMessage() {
        return message;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof StartedConversationEvent)) return false;
        final StartedConversationEvent that = (StartedConversationEvent) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(participantsIdentifier, that.participantsIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, participantsIdentifier);
    }

    @Override
    public String toString() {
        return "StartConversationEvent{" +
                "message=" + message +
                ", participantsIdentifier=" + participantsIdentifier +
                '}';
    }
}
