package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationRepository;
import com.innerfriends.messaging.domain.Message;
import com.innerfriends.messaging.domain.UseCase;

import java.util.Objects;

public class PostResponseToConversationUseCase implements UseCase<Message, PostResponseToConversationCommand> {

    private final ConversationRepository conversationRepository;

    public PostResponseToConversationUseCase(final ConversationRepository conversationRepository) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
    }

    @Override
    public Message execute(final PostResponseToConversationCommand command) {
        final Conversation conversation = this.conversationRepository.getConversation(command.conversationIdentifier());
        final Message messagePosted = conversation.post(command.from(), command.postedAt(), command.content());
        this.conversationRepository.createConversation(messagePosted);
        return messagePosted;
    }

}
