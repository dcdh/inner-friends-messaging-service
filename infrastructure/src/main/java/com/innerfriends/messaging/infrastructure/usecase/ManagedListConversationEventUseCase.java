package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.ConversationEvent;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.ListConversationEventCommand;
import com.innerfriends.messaging.domain.usecase.ListConversationEventUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ManagedListConversationEventUseCase implements UseCase<List<ConversationEvent>, ListConversationEventCommand> {

    private final ListConversationEventUseCase listConversationEventUseCase;

    public ManagedListConversationEventUseCase(final ListConversationEventUseCase listConversationEventUseCase) {
        this.listConversationEventUseCase = Objects.requireNonNull(listConversationEventUseCase);
    }

    @Transactional
    @Override
    public List<ConversationEvent> execute(final ListConversationEventCommand command) {
        return listConversationEventUseCase.execute(command);
    }

}
