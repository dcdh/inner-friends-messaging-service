package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;

import java.util.Objects;
import java.util.stream.Collectors;

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
        final AddedAt addedAt = new AddedAt(createdAt.at());
        final ContactBook contactBook = new ContactBook(createContactBookCommand.owner(), createdAt,
                createContactBookCommand.contactIdentifiers().stream()
                        .map(contactIdentifier -> new Contact(contactIdentifier, addedAt))
                        .collect(Collectors.toList()));
        contactBookRepository.save(contactBook);
        return contactBook;
    }

}
