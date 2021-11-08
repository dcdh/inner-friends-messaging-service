package com.innerfriends.messaging.infrastructure.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.OpenANewConversationCommand;
import com.innerfriends.messaging.domain.usecase.OpenANewConversationUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.outbox.conversation.NewConversationOpenedEvent;
import io.debezium.outbox.quarkus.ExportedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class ManagedOpenANewConversationUseCase implements UseCase<Conversation, OpenANewConversationCommand> {

    private final OpenANewConversationUseCase openANewConversationUseCase;
    private final Event<ExportedEvent<?, ?>> event;
    private final ObjectMapper objectMapper;
    private final InstantProvider instantProvider;

    public ManagedOpenANewConversationUseCase(final OpenANewConversationUseCase openANewConversationUseCase,
                                              final Event<ExportedEvent<?, ?>> event,
                                              final ObjectMapper objectMapper,
                                              final InstantProvider instantProvider) {
        this.openANewConversationUseCase = Objects.requireNonNull(openANewConversationUseCase);
        this.event = Objects.requireNonNull(event);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.instantProvider = Objects.requireNonNull(instantProvider);
    }

    @Transactional
    @Override
    public Conversation execute(final OpenANewConversationCommand command) {
        final Conversation conversationOpened = openANewConversationUseCase.execute(command);
        event.fire(NewConversationOpenedEvent.of(conversationOpened, objectMapper, instantProvider));
        return conversationOpened;
    }

}
