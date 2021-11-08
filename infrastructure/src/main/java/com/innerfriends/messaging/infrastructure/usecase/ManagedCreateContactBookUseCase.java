package com.innerfriends.messaging.infrastructure.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.CreateContactBookCommand;
import com.innerfriends.messaging.domain.usecase.CreateContactBookUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.outbox.contactbook.ContactBookCreatedEvent;
import io.debezium.outbox.quarkus.ExportedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class ManagedCreateContactBookUseCase implements UseCase<ContactBook, CreateContactBookCommand> {

    private final CreateContactBookUseCase createContactBookUseCase;
    private final Event<ExportedEvent<?, ?>> event;
    private final ObjectMapper objectMapper;
    private final InstantProvider instantProvider;

    public ManagedCreateContactBookUseCase(final CreateContactBookUseCase createContactBookUseCase,
                                           final Event<ExportedEvent<?, ?>> event,
                                           final ObjectMapper objectMapper,
                                           final InstantProvider instantProvider) {
        this.createContactBookUseCase = Objects.requireNonNull(createContactBookUseCase);
        this.event = Objects.requireNonNull(event);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.instantProvider = Objects.requireNonNull(instantProvider);
    }

    @Transactional
    @Override
    public ContactBook execute(final CreateContactBookCommand command) {
        final ContactBook contactBookNewlyCreated = createContactBookUseCase.execute(command);
        event.fire(ContactBookCreatedEvent.of(contactBookNewlyCreated, objectMapper, instantProvider));
        return contactBookNewlyCreated;
    }

}
