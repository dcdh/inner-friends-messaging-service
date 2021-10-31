package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ContactBookRepository;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.UseCase;

import java.util.Objects;

public class CreateContactBookUseCase implements UseCase<ContactBook, CreateContactBookCommand> {

    private final ContactBookRepository contactBookRepository;

    public CreateContactBookUseCase(final ContactBookRepository contactBookRepository) {
        this.contactBookRepository = Objects.requireNonNull(contactBookRepository);
    }

    @Override
    public ContactBook execute(final CreateContactBookCommand createContactBookCommand) {
        final ContactBook contactBook = new ContactBook(new Owner(createContactBookCommand.contactIdentifier()));
        contactBookRepository.save(contactBook);
        return contactBook;
    }

}
