package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.AddedAtProvider;
import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationRepository;
import com.innerfriends.messaging.domain.UseCase;

import java.util.Objects;

public final class AddParticipantIntoConversationUseCase implements UseCase<Conversation, AddParticipantIntoConversationCommand> {

    private final ConversationRepository conversationRepository;
    private final AddedAtProvider addedAtProvider;

    public AddParticipantIntoConversationUseCase(final ConversationRepository conversationRepository,
                                                 final AddedAtProvider addedAtProvider) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
        this.addedAtProvider = Objects.requireNonNull(addedAtProvider);
    }

    @Override
    public Conversation execute(final AddParticipantIntoConversationCommand command) {
        final Conversation conversationToAddNewParticipant = this.conversationRepository.getConversation(command.conversationIdentifier());
        final Conversation conversationToSave = conversationToAddNewParticipant
                .addAParticipantIntoConversation(command.participantIdentifier(), addedAtProvider.now());
        this.conversationRepository.saveConversation(conversationToSave);
        return conversationToSave;
    }
}
