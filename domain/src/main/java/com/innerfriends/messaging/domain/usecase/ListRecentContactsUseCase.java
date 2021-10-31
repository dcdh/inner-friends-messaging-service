package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactBookRepository;
import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.UseCase;

import java.util.List;
import java.util.Objects;

public class ListRecentContactsUseCase implements UseCase<List<ContactIdentifier>, ListRecentContactsCommand> {

    private final ContactBookRepository contactBookRepository;

    public ListRecentContactsUseCase(final ContactBookRepository contactBookRepository) {
        this.contactBookRepository = Objects.requireNonNull(contactBookRepository);
    }

    @Override
    public List<ContactIdentifier> execute(final ListRecentContactsCommand command) {
        return contactBookRepository.getByOwner(command.owner())
                .recentContacts(command.nbOfContactToReturn());
    }

}
