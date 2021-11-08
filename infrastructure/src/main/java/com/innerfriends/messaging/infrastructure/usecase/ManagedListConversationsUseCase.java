package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.ListConversationsCommand;
import com.innerfriends.messaging.domain.usecase.ListConversationsUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ManagedListConversationsUseCase implements UseCase<List<Conversation>, ListConversationsCommand> {

    private final ListConversationsUseCase listConversationsUseCase;

    public ManagedListConversationsUseCase(final ListConversationsUseCase listConversationsUseCase) {
        this.listConversationsUseCase = Objects.requireNonNull(listConversationsUseCase);
    }

    @Transactional
    @Override
    public List<Conversation> execute(final ListConversationsCommand command) {
        return listConversationsUseCase.execute(command);
    }

}
