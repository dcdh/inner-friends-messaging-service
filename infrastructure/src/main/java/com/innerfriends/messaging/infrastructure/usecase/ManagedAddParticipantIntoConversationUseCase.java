package com.innerfriends.messaging.infrastructure.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.usecase.AddParticipantIntoConversationCommand;
import com.innerfriends.messaging.domain.usecase.AddParticipantIntoConversationUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.outbox.conversation.ParticipantAddedIntoConversationEvent;
import io.debezium.outbox.quarkus.ExportedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.transaction.Transactional;

@ApplicationScoped
public class ManagedAddParticipantIntoConversationUseCase implements UseCase<Conversation, AddParticipantIntoConversationCommand> {

    private final AddParticipantIntoConversationUseCase addParticipantIntoConversationUseCase;
    private final Event<ExportedEvent<?, ?>> event;
    private final ObjectMapper objectMapper;
    private final InstantProvider instantProvider;

    public ManagedAddParticipantIntoConversationUseCase(final AddParticipantIntoConversationUseCase addParticipantIntoConversationUseCase,
                                                        final Event<ExportedEvent<?, ?>> event,
                                                        final ObjectMapper objectMapper,
                                                        final InstantProvider instantProvider) {
        this.addParticipantIntoConversationUseCase = addParticipantIntoConversationUseCase;
        this.event = event;
        this.objectMapper = objectMapper;
        this.instantProvider = instantProvider;
    }

    @Transactional
    @Override
    public Conversation execute(final AddParticipantIntoConversationCommand command) {
        final Conversation conversation = addParticipantIntoConversationUseCase.execute(command);
        event.fire(ParticipantAddedIntoConversationEvent.of(conversation, objectMapper, instantProvider));
        return conversation;
    }

}

