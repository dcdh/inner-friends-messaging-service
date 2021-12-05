package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.AddedAtProvider;
import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ContactBookRepository;
import com.innerfriends.messaging.domain.UseCase;

import java.util.Objects;

public class AddContactIntoContactBookUseCase implements UseCase<ContactBook, AddContactIntoContactBookCommand> {

    private final ContactBookRepository contactBookRepository;
    private final AddedAtProvider addedAtProvider;

    public AddContactIntoContactBookUseCase(final ContactBookRepository contactBookRepository,
                                            final AddedAtProvider addedAtProvider) {
        this.contactBookRepository = Objects.requireNonNull(contactBookRepository);
        this.addedAtProvider = Objects.requireNonNull(addedAtProvider);
    }

    @Override
    public ContactBook execute(final AddContactIntoContactBookCommand command) {
        final ContactBook contactBook = this.contactBookRepository.getByOwner(command.owner());
        contactBook.addNewContact(command.contactIdentifier(), addedAtProvider.now());
        this.contactBookRepository.save(contactBook);
        return contactBook;
    }

}
