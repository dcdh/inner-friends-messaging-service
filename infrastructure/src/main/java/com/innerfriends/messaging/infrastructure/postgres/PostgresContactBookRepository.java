package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ContactBookRepository;
import com.innerfriends.messaging.domain.NoContactBookFoundException;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.infrastructure.opentelemetry.NewSpan;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class PostgresContactBookRepository implements ContactBookRepository {

    private final EntityManager entityManager;

    public PostgresContactBookRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @NewSpan
    @Transactional(value = Transactional.TxType.MANDATORY, dontRollbackOn = {NoContactBookFoundException.class})
    @Override
    public ContactBook getByOwner(final Owner owner) throws NoContactBookFoundException {
        return Optional.ofNullable(entityManager.find(ContactBookEntity.class, owner.identifier().identifier(), LockModeType.PESSIMISTIC_WRITE))
                .map(ContactBookEntity::toContactBook)
                .orElseThrow(() -> new NoContactBookFoundException());
    }

    @NewSpan
    @Transactional(value = Transactional.TxType.MANDATORY)
    @Override
    public void save(final ContactBook contactBook) {
        final ContactBookEntity contactBookEntity = new ContactBookEntity(contactBook);
        this.entityManager.merge(contactBookEntity);
    }

}
