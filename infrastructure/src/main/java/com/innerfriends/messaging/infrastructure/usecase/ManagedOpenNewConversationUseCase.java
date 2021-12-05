package com.innerfriends.messaging.infrastructure.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.OpenNewConversationCommand;
import com.innerfriends.messaging.domain.usecase.OpenNewConversationUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.outbox.conversation.NewConversationOpenedEvent;
import io.debezium.outbox.quarkus.ExportedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class ManagedOpenNewConversationUseCase implements UseCase<Conversation, OpenNewConversationCommand> {

    private final OpenNewConversationUseCase openNewConversationUseCase;
    private final Event<ExportedEvent<?, ?>> event;
    private final ObjectMapper objectMapper;
    private final InstantProvider instantProvider;

    public ManagedOpenNewConversationUseCase(final OpenNewConversationUseCase openNewConversationUseCase,
                                             final Event<ExportedEvent<?, ?>> event,
                                             final ObjectMapper objectMapper,
                                             final InstantProvider instantProvider) {
        this.openNewConversationUseCase = Objects.requireNonNull(openNewConversationUseCase);
        this.event = Objects.requireNonNull(event);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.instantProvider = Objects.requireNonNull(instantProvider);
    }

    @Transactional
    @Override
    public Conversation execute(final OpenNewConversationCommand command) {
        final Conversation conversationOpened = openNewConversationUseCase.execute(command);
        event.fire(NewConversationOpenedEvent.of(conversationOpened, objectMapper, instantProvider));
        return conversationOpened;
    }

}
