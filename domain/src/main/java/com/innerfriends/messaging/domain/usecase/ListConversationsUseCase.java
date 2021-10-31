package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationRepository;
import com.innerfriends.messaging.domain.Conversations;
import com.innerfriends.messaging.domain.UseCase;

import java.util.List;
import java.util.Objects;

public class ListConversationsUseCase implements UseCase<List<Conversation>, ListConversationsCommand> {

    private final ConversationRepository conversationRepository;

    public ListConversationsUseCase(final ConversationRepository conversationRepository) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
    }

    @Override
    public List<Conversation> execute(final ListConversationsCommand listConversationsCommand) {
        final List<Conversation> conversations = conversationRepository
                .getConversationsForParticipant(listConversationsCommand.participantIdentifier());
        return new Conversations(listConversationsCommand.participantIdentifier(),
                conversations).listByLastInteraction();
    }

}
