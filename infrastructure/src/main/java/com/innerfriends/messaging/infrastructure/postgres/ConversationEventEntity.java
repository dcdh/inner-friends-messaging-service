package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.*;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection
public final class ConversationEventEntity {

    public ConversationEventType conversationEventType;

    public String eventFrom;

    public String eventAt;

    public String content;

    public List<String> participantsIdentifier;

    public ConversationEventEntity() {}

    public ConversationEventEntity(final ConversationEvent conversationEvent) {
        this.conversationEventType = Objects.requireNonNull(conversationEvent.conversationEventType());
        this.eventFrom = Objects.requireNonNull(conversationEvent.eventFrom().identifier().identifier());
        this.eventAt = Objects.requireNonNull(conversationEvent.eventAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        this.content = Objects.requireNonNull(conversationEvent.content().content());
        this.participantsIdentifier = Objects.requireNonNull(conversationEvent.participantsIdentifier().stream()
                .map(ParticipantIdentifier::identifier)
                .collect(Collectors.toList()));
    }

    public ConversationEvent toConversationEvent() {
        return conversationEventType.toConversationEvent(new EventFrom(new ParticipantIdentifier(eventFrom)),
                new EventAt(ZonedDateTime.parse(eventAt, DateTimeFormatter.ISO_ZONED_DATE_TIME)),
                new Content(content),
                participantsIdentifier.stream()
                        .map(ParticipantIdentifier::new)
                        .collect(Collectors.toList()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationEventEntity)) return false;
        final ConversationEventEntity that = (ConversationEventEntity) o;
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
