package com.innerfriends.messaging.infrastructure.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.AddContactIntoContactBookCommand;
import com.innerfriends.messaging.domain.usecase.AddContactIntoContactBookUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.outbox.contactbook.ContactAddedIntoContactBookEvent;
import com.innerfriends.messaging.infrastructure.usecase.cache.ContactBookCacheRepository;
import io.debezium.outbox.quarkus.ExportedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class ManagedAddContactIntoContactBookUseCase implements UseCase<ContactBook, AddContactIntoContactBookCommand> {

    private final AddContactIntoContactBookUseCase addContactIntoContactBookUseCase;
    private final ContactBookCacheRepository contactBookCacheRepository;
    private final Event<ExportedEvent<?, ?>> event;
    private final ObjectMapper objectMapper;
    private final InstantProvider instantProvider;

    public ManagedAddContactIntoContactBookUseCase(final AddContactIntoContactBookUseCase addContactIntoContactBookUseCase,
                                                   final ContactBookCacheRepository contactBookCacheRepository,
                                                   final Event<ExportedEvent<?, ?>> event,
                                                   final ObjectMapper objectMapper,
                                                   final InstantProvider instantProvider) {
        this.addContactIntoContactBookUseCase = Objects.requireNonNull(addContactIntoContactBookUseCase);
        this.contactBookCacheRepository = Objects.requireNonNull(contactBookCacheRepository);
        this.event = Objects.requireNonNull(event);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.instantProvider = Objects.requireNonNull(instantProvider);
    }

    @Transactional
    @Override
    public ContactBook execute(final AddContactIntoContactBookCommand command) {
        contactBookCacheRepository.evict(command.owner());
        final ContactBook contactBookWithNewAddedContact = addContactIntoContactBookUseCase.execute(command);
        event.fire(ContactAddedIntoContactBookEvent.of(contactBookWithNewAddedContact, objectMapper, instantProvider));
        return contactBookWithNewAddedContact;
    }

}
