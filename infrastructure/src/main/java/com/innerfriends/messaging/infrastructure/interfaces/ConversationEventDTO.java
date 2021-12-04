package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.ConversationEvent;
import com.innerfriends.messaging.domain.ConversationEventType;
import com.innerfriends.messaging.domain.ParticipantIdentifier;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection
public final class ConversationEventDTO {

    private final ConversationEventType conversationEventType;

    private final String eventFrom;

    private final ZonedDateTime eventAt;

    private final String content;

    private final List<String> participantsIdentifier;

    public ConversationEventDTO(final ConversationEvent conversationEvent) {
        this.conversationEventType = conversationEvent.conversationEventType();
        this.eventFrom = conversationEvent.eventFrom().identifier().identifier();
        this.eventAt = conversationEvent.eventAt().at();
        this.content = conversationEvent.content().content();
        this.participantsIdentifier = conversationEvent.participantsIdentifier()
                .stream()
                .map(ParticipantIdentifier::identifier)
                .collect(Collectors.toList());
    }

    public ConversationEventType getConversationEventType() {
        return conversationEventType;
    }

    public String getEventFrom() {
        return eventFrom;
    }

    public ZonedDateTime getEventAt() {
        return eventAt;
    }

    public String getContent() {
        return content;
    }

    public List<String> getParticipantsIdentifier() {
        return participantsIdentifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationEventDTO)) return false;
        final ConversationEventDTO that = (ConversationEventDTO) o;
        return conversationEventType == that.conversationEventType &&
                Objects.equals(eventFrom, that.eventFrom) &&
                Objects.equals(eventAt, that.eventAt) &&
                Objects.equals(content, that.content) &&
                Objects.equals(participantsIdentifier, that.participantsIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationEventType, eventFrom, eventAt, content, participantsIdentifier);
    }
}
