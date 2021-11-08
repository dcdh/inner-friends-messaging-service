package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.Message;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.ListMessagesInConversationCommand;
import com.innerfriends.messaging.domain.usecase.ListMessagesInConversationUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ManagedListMessagesInConversationUseCase implements UseCase<List<Message>, ListMessagesInConversationCommand> {

    private final ListMessagesInConversationUseCase listMessagesInConversationUseCase;

    public ManagedListMessagesInConversationUseCase(final ListMessagesInConversationUseCase listMessagesInConversationUseCase) {
        this.listMessagesInConversationUseCase = Objects.requireNonNull(listMessagesInConversationUseCase);
    }

    @Transactional
    @Override
    public List<Message> execute(final ListMessagesInConversationCommand command) {
        return listMessagesInConversationUseCase.execute(command);
    }

}
