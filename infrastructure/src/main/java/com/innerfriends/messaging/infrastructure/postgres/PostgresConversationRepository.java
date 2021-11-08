package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.infrastructure.opentelemetry.NewSpan;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class PostgresConversationRepository implements ConversationRepository {

    private final EntityManager entityManager;

    public PostgresConversationRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @NewSpan
    @Transactional(value = Transactional.TxType.MANDATORY, dontRollbackOn = {UnknownConversationException.class})
    @Override
    public Conversation getConversation(final ConversationIdentifier conversationIdentifier) throws UnknownConversationException {
        return Optional.ofNullable(entityManager.find(ConversationEntity.class, conversationIdentifier.identifier(), LockModeType.PESSIMISTIC_WRITE))
                .map(ConversationEntity::toConversation)
                .orElseThrow(() -> new UnknownConversationException(conversationIdentifier));
    }

    @NewSpan
    @Transactional(value = Transactional.TxType.MANDATORY)
    @Override
    public void createConversation(final Conversation conversation) {
        entityManager.persist(new ConversationEntity(conversation));
    }

    @NewSpan
    @Override
    // https://stackoverflow.com/a/58981070/2570225
    public List<Conversation> getConversationsForParticipant(final ParticipantIdentifier participantIdentifier) {
        final String query = String.format("SELECT * FROM T_CONVERSATION WHERE participantidentifiers @> '\"%s\"'", participantIdentifier.identifier());
        final Stream<ConversationEntity> stream = entityManager.createNativeQuery(query, ConversationEntity.class)
                .getResultStream();
        return stream.map(ConversationEntity::toConversation).collect(Collectors.toList());
    }

    @NewSpan
    @Transactional(value = Transactional.TxType.MANDATORY)
    @Override
    public void saveConversation(final Conversation conversation) {
        entityManager.merge(new ConversationEntity(conversation));
    }

}
