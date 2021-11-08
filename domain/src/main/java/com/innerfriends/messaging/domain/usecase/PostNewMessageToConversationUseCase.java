package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationRepository;
import com.innerfriends.messaging.domain.PostedAtProvider;
import com.innerfriends.messaging.domain.UseCase;

import java.util.Objects;

public class PostNewMessageToConversationUseCase implements UseCase<Conversation, PostNewMessageToConversationCommand> {

    private final ConversationRepository conversationRepository;
    private final PostedAtProvider postedAtProvider;

    public PostNewMessageToConversationUseCase(final ConversationRepository conversationRepository,
                                               final PostedAtProvider postedAtProvider) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
        this.postedAtProvider = Objects.requireNonNull(postedAtProvider);
    }

    @Override
    public Conversation execute(final PostNewMessageToConversationCommand command) {
        final Conversation conversationToPostNewMessage = this.conversationRepository.getConversation(command.conversationIdentifier());
        final Conversation conversationToSave = conversationToPostNewMessage
                .post(command.from(), postedAtProvider.now(), command.content());
        this.conversationRepository.saveConversation(conversationToSave);
        return conversationToSave;
    }

}
