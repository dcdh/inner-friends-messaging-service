package com.innerfriends.messaging.infrastructure.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.PostNewMessageToConversationCommand;
import com.innerfriends.messaging.domain.usecase.PostNewMessageToConversationUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.outbox.conversation.NewMessagePostedToConversationEvent;
import io.debezium.outbox.quarkus.ExportedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class ManagedPostNewMessageToConversationUseCase implements UseCase<Conversation, PostNewMessageToConversationCommand> {

    private final PostNewMessageToConversationUseCase postNewMessageToConversationUseCase;
    private final Event<ExportedEvent<?, ?>> event;
    private final ObjectMapper objectMapper;
    private final InstantProvider instantProvider;

    public ManagedPostNewMessageToConversationUseCase(final PostNewMessageToConversationUseCase postNewMessageToConversationUseCase,
                                                      final Event<ExportedEvent<?, ?>> event,
                                                      final ObjectMapper objectMapper,
                                                      final InstantProvider instantProvider) {
        this.postNewMessageToConversationUseCase = Objects.requireNonNull(postNewMessageToConversationUseCase);
        this.event = Objects.requireNonNull(event);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.instantProvider = Objects.requireNonNull(instantProvider);
    }

    @Transactional
    @Override
    public Conversation execute(final PostNewMessageToConversationCommand command) {
        final Conversation conversationWithNewMessagePosted = postNewMessageToConversationUseCase.execute(command);
        event.fire(NewMessagePostedToConversationEvent.of(conversationWithNewMessagePosted, objectMapper, instantProvider));
        return conversationWithNewMessagePosted;
    }

}
