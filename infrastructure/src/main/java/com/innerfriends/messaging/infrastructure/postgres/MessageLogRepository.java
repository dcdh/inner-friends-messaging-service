package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.infrastructure.opentelemetry.NewSpan;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class MessageLogRepository {

    private final EntityManager entityManager;

    public MessageLogRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @NewSpan
    @Transactional(value=TxType.MANDATORY)
    public void markAsConsumed(final String groupId, final UUID eventId, final Instant instant) {
        entityManager.persist(new ConsumedMessageEntity(groupId, eventId, instant));
    }

    @NewSpan
    @Transactional(value=TxType.MANDATORY)
    public boolean alreadyProcessed(final String groupId, final UUID eventId) {
        return entityManager.createQuery("SELECT CASE WHEN (COUNT(*) > 0) THEN TRUE ELSE FALSE END FROM ConsumedMessageEntity " +
                "WHERE consumedMessageId.groupId = :groupId AND consumedMessageId.eventId = :eventId", Boolean.class)
                .setParameter("groupId", groupId)
                .setParameter("eventId", eventId)
                .getSingleResult();
    }
}
