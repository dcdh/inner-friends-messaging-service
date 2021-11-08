package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactBookRepository;
import com.innerfriends.messaging.domain.ListAllContactInContactBook;
import com.innerfriends.messaging.domain.UseCase;

import java.util.Objects;

public class ListAllContactsUseCase implements UseCase<ListAllContactInContactBook, ListAllContactsCommand> {

    private final ContactBookRepository contactBookRepository;

    public ListAllContactsUseCase(final ContactBookRepository contactBookRepository) {
        this.contactBookRepository = Objects.requireNonNull(contactBookRepository);
    }

    @Override
    public ListAllContactInContactBook execute(final ListAllContactsCommand command) {
        return new ListAllContactInContactBook(this.contactBookRepository.getByOwner(command.owner()));
    }

}
