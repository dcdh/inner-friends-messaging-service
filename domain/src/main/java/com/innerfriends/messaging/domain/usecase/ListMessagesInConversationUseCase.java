package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ConversationRepository;
import com.innerfriends.messaging.domain.Message;
import com.innerfriends.messaging.domain.UseCase;

import java.util.List;
import java.util.Objects;

public class ListMessagesInConversationUseCase implements UseCase<List<Message>, ListMessagesInConversationCommand> {

    private final ConversationRepository conversationRepository;

    public ListMessagesInConversationUseCase(final ConversationRepository conversationRepository) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
    }

    @Override
    public List<Message> execute(final ListMessagesInConversationCommand command) {
        return conversationRepository.getConversation(command.conversationIdentifier()).messages();
    }

}
