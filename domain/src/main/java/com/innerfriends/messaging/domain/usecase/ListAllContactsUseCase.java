package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactBookRepository;
import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.UseCase;

import java.util.List;
import java.util.Objects;

public class ListAllContactsUseCase implements UseCase<List<ContactIdentifier>, ListAllContactsCommand> {

    private final ContactBookRepository contactBookRepository;

    public ListAllContactsUseCase(final ContactBookRepository contactBookRepository) {
        this.contactBookRepository = Objects.requireNonNull(contactBookRepository);
    }

    @Override
    public List<ContactIdentifier> execute(final ListAllContactsCommand command) {
        return this.contactBookRepository.getByOwner(command.owner()).allContacts();
    }

}
