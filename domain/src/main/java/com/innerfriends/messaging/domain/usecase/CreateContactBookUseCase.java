package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;

import java.util.Objects;

public class CreateContactBookUseCase implements UseCase<ContactBook, CreateContactBookCommand> {

    private final ContactBookRepository contactBookRepository;
    private final CreatedAtProvider createdAtProvider;

    public CreateContactBookUseCase(final ContactBookRepository contactBookRepository,
                                    final CreatedAtProvider createdAtProvider) {
        this.contactBookRepository = Objects.requireNonNull(contactBookRepository);
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
    }

    @Override
    public ContactBook execute(final CreateContactBookCommand createContactBookCommand) {
        final CreatedAt createdAt = createdAtProvider.now();
        final ContactBook contactBook = new ContactBook(createContactBookCommand.owner(), createdAt);
        contactBookRepository.save(contactBook);
        return contactBook;
    }

}
