package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationRepository;
import com.innerfriends.messaging.domain.UseCase;
import com.innerfriends.messaging.domain.YouAreNotAParticipantException;

import java.util.Objects;

public class PostResponseToConversationUseCase implements UseCase<Conversation, PostResponseToConversationCommand> {

    private final ConversationRepository conversationRepository;

    public PostResponseToConversationUseCase(final ConversationRepository conversationRepository) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
    }

    @Override
    public Conversation execute(final PostResponseToConversationCommand command) {
        final Conversation conversationToPostResponse = this.conversationRepository.getConversation(command.conversationIdentifier());
        final Conversation conversationToSave = conversationToPostResponse
                .post(command.from(), command.postedAt(), command.content());
        this.conversationRepository.saveConversation(conversationToSave);
        return conversationToSave;
    }

}
