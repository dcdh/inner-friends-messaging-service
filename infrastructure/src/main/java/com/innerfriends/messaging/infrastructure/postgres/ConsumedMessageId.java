package com.innerfriends.messaging.infrastructure.postgres;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public final class ConsumedMessageId implements Serializable {

    public String groupId;

    public UUID eventId;

    public ConsumedMessageId() {}

    public ConsumedMessageId(final String groupId, final UUID eventId) {
        this.groupId = Objects.requireNonNull(groupId);
        this.eventId = Objects.requireNonNull(eventId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsumedMessageId)) return false;
        final ConsumedMessageId that = (ConsumedMessageId) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, eventId);
    }

    @Override
    public String toString() {
        return "ConsumedMessageId{" +
                "groupId='" + groupId + '\'' +
                ", eventId=" + eventId +
                '}';
    }
}
