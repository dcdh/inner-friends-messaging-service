package com.innerfriends.messaging.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class MessagePostedConversationEvent implements ConversationEvent {

    private final Message message;

    public MessagePostedConversationEvent(final Message message) {
        this.message = Objects.requireNonNull(message);
    }

    @Override
    public ConversationEventType conversationEventType() {
        return ConversationEventType.MESSAGE_POSTED;
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
        return Collections.emptyList();
    }

    @Override
    public Message toMessage() {
        return message;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessagePostedConversationEvent)) return false;
        final MessagePostedConversationEvent that = (MessagePostedConversationEvent) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "MessagePostedConversationEvent{" +
                "message=" + message +
                '}';
    }
}
