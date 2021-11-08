package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.ListAllContactInContactBook;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.ListAllContactsCommand;
import com.innerfriends.messaging.domain.usecase.ListAllContactsUseCase;
import com.innerfriends.messaging.infrastructure.usecase.cache.ContactBookCacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class ManagedListAllContactsUseCase implements UseCase<ListAllContactInContactBook, ListAllContactsCommand> {

    private final ListAllContactsUseCase listAllContactsUseCase;
    private final ContactBookCacheRepository contactBookCacheRepository;

    public ManagedListAllContactsUseCase(final ListAllContactsUseCase listAllContactsUseCase,
                                         final ContactBookCacheRepository contactBookCacheRepository) {
        this.listAllContactsUseCase = Objects.requireNonNull(listAllContactsUseCase);
        this.contactBookCacheRepository = Objects.requireNonNull(contactBookCacheRepository);
    }

    @Transactional
    @Override
    public ListAllContactInContactBook execute(final ListAllContactsCommand command) {
        return contactBookCacheRepository.getByOwner(command.owner())
                .map(ListAllContactInContactBook::new)
                .orElseGet(() -> {
                    final ListAllContactInContactBook listAllContactInContactBook = listAllContactsUseCase.execute(command);
                    contactBookCacheRepository.store(listAllContactInContactBook.contactBook());
                    return listAllContactInContactBook;
                });
    }

}
