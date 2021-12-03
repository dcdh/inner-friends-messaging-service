package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.*;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RegisterForReflection
public final class ConversationEventEntity {

    public ConversationEventType conversationEventType;

    public String eventFrom;

    public String eventAt;

    public String content;

    public ConversationEventEntity() {}

    public ConversationEventEntity(final ConversationEvent conversationEvent) {
        this.conversationEventType = Objects.requireNonNull(conversationEvent.conversationEventType());
        this.eventFrom = Objects.requireNonNull(conversationEvent.eventFrom().identifier().identifier());
        this.eventAt = Objects.requireNonNull(conversationEvent.eventAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        this.content = Objects.requireNonNull(conversationEvent.content().content());
    }

    public ConversationEvent toConversationEvent() {
        return conversationEventType.toConversationEvent(new EventFrom(new ParticipantIdentifier(eventFrom)),
                new EventAt(ZonedDateTime.parse(eventAt, DateTimeFormatter.ISO_ZONED_DATE_TIME)),
                new Content(content));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationEventEntity)) return false;
        final ConversationEventEntity that = (ConversationEventEntity) o;
        return conversationEventType == that.conversationEventType &&
                Objects.equals(eventFrom, that.eventFrom) &&
                Objects.equals(eventAt, that.eventAt) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationEventType, eventFrom, eventAt, content);
    }
}
