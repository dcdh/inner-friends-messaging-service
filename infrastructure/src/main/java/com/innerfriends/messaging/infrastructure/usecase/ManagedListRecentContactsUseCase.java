package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.ListRecentContactsCommand;
import com.innerfriends.messaging.domain.usecase.ListRecentContactsUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ManagedListRecentContactsUseCase implements UseCase<List<ContactIdentifier>, ListRecentContactsCommand> {

    private final ListRecentContactsUseCase listRecentContactsUseCase;

    public ManagedListRecentContactsUseCase(final ListRecentContactsUseCase listRecentContactsUseCase) {
        this.listRecentContactsUseCase = Objects.requireNonNull(listRecentContactsUseCase);
    }

    @Transactional
    @Override
    public List<ContactIdentifier> execute(final ListRecentContactsCommand command) {
        return listRecentContactsUseCase.execute(command);
    }

}
