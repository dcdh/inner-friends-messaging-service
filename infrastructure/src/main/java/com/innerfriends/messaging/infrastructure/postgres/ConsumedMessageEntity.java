package com.innerfriends.messaging.infrastructure.postgres;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "T_CONSUMED_MESSAGE"
)
public class ConsumedMessageEntity {

    @EmbeddedId
    public ConsumedMessageId consumedMessageId;

    @NotNull
    public Instant timeOfReceiving;

    public ConsumedMessageEntity() {}

    public ConsumedMessageEntity(final String groupId,
                                 final UUID eventId,
                                 final Instant timeOfReceiving) {
        this.consumedMessageId = new ConsumedMessageId(groupId, eventId);
        this.timeOfReceiving = Objects.requireNonNull(timeOfReceiving);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsumedMessageEntity)) return false;
        final ConsumedMessageEntity that = (ConsumedMessageEntity) o;
        return Objects.equals(consumedMessageId, that.consumedMessageId) &&
                Objects.equals(timeOfReceiving, that.timeOfReceiving);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consumedMessageId, timeOfReceiving);
    }

    @Override
    public String toString() {
        return "ConsumedMessageEntity{" +
                "consumedMessageId=" + consumedMessageId +
                ", timeOfReceiving=" + timeOfReceiving +
                '}';
    }
}
