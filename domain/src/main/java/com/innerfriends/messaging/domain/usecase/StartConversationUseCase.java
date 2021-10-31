package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StartConversationUseCase implements UseCase<Conversation, StartConversationCommand> {

    private final ConversationRepository conversationRepository;
    private final ContactBookRepository contactBookRepository;
    private final ConversationIdentifierProvider conversationIdentifierProvider;

    public StartConversationUseCase(final ConversationRepository conversationRepository,
                                    final ContactBookRepository contactBookRepository,
                                    final ConversationIdentifierProvider conversationIdentifierProvider) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
        this.contactBookRepository = Objects.requireNonNull(contactBookRepository);
        this.conversationIdentifierProvider = Objects.requireNonNull(conversationIdentifierProvider);
    }

    @Override
    public Conversation execute(final StartConversationCommand startConversationCommand) {
        final ContactBook contactBook = this.contactBookRepository.getByOwner(new Owner(startConversationCommand.from()));
        if (!contactBook.hasContact(startConversationCommand.to())) {
            throw new ToIsNotInContactBookException(startConversationCommand.from(), startConversationCommand.to());
        }
        final ConversationIdentifier conversationIdentifier = conversationIdentifierProvider.generate(startConversationCommand.from());
        final Conversation conversation = new Conversation(conversationIdentifier,
                List.of(new Message(
                        startConversationCommand.from(),
                        new PostedAt(startConversationCommand.startAt()),
                        startConversationCommand.content())),
                Arrays.asList(startConversationCommand.from().identifier(),
                        startConversationCommand.to().identifier()));
        this.conversationRepository.save(conversation);
        return conversation;
    }

}
