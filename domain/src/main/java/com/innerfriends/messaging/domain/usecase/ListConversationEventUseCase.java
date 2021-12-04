package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ConversationEvent;
import com.innerfriends.messaging.domain.ConversationRepository;
import com.innerfriends.messaging.domain.UseCase;

import java.util.List;
import java.util.Objects;

public class ListConversationEventUseCase implements UseCase<List<ConversationEvent>, ListConversationEventCommand> {

    private final ConversationRepository conversationRepository;

    public ListConversationEventUseCase(final ConversationRepository conversationRepository) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
    }

    @Override
    public List<ConversationEvent> execute(final ListConversationEventCommand command) {
        return conversationRepository.getConversation(command.conversationIdentifier()).events();
    }

}
