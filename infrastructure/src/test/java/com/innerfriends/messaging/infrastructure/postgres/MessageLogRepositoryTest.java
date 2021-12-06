package com.innerfriends.messaging.infrastructure.postgres;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.RollbackException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class MessageLogRepositoryTest extends RepositoryTesting {

    public static final String COUNT_MESSAGE_LOG_SQL = "SELECT COUNT(*) FROM public.T_CONSUMED_MESSAGE";

    @Inject
    EntityManager entityManager;

    @Inject
    MessageLogRepository messageLogRepository;

    @Test
    public void should_mark_as_consumed() throws Exception {
        // Given
        final Instant instant = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        // When
        runInTransaction(() -> {
            messageLogRepository.markAsConsumed("groupId", new UUID(0, 0), instant);
            return null;
        });

        // Then
        assertThat(((Number) entityManager.createNativeQuery(COUNT_MESSAGE_LOG_SQL)
                .getSingleResult()).longValue()).isEqualTo(1l);
        assertThat(entityManager.find(ConsumedMessageEntity.class, new ConsumedMessageId("groupId", new UUID(0, 0))))
                .isEqualTo(new ConsumedMessageEntity("groupId", new UUID(0, 0), instant));
    }

    @Test
    public void should_fail_if_already_marked_as_consumed() throws Exception {
        // Given
        final Instant instant = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        runInTransaction(() -> {
            messageLogRepository.markAsConsumed("groupId", new UUID(0, 0), instant);
            return null;
        });

        // When && Then
        assertThatThrownBy(() -> runInTransaction(() -> {
            messageLogRepository.markAsConsumed("groupId", new UUID(0, 0), instant);
            return null;
        }))
                .isInstanceOf(RollbackException.class);
    }

    @Test
    public void should_not_be_consumed_yet() throws Exception {
        // Given

        // When
        final boolean consumed = runInTransaction(() -> messageLogRepository.alreadyProcessed("groupId", new UUID(0, 0)));

        // Then
        assertThat(consumed).isFalse();
    }

    @Test
    public void should_be_already_consumed() throws Exception {
        // Given
        final Instant instant = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        runInTransaction(() -> {
            messageLogRepository.markAsConsumed("groupId", new UUID(0, 0), instant);
            return null;
        });

        // When
        final boolean consumed = runInTransaction(() -> messageLogRepository.alreadyProcessed("groupId", new UUID(0, 0)));

        // Then
        assertThat(consumed).isTrue();
    }

}
