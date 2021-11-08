package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenANewConversationUseCase implements UseCase<Conversation, OpenANewConversationCommand> {

    private final ConversationRepository conversationRepository;
    private final ContactBookRepository contactBookRepository;
    private final ConversationIdentifierProvider conversationIdentifierProvider;
    private final PostedAtProvider postedAtProvider;

    public OpenANewConversationUseCase(final ConversationRepository conversationRepository,
                                       final ContactBookRepository contactBookRepository,
                                       final ConversationIdentifierProvider conversationIdentifierProvider,
                                       final PostedAtProvider postedAtProvider) {
        this.conversationRepository = Objects.requireNonNull(conversationRepository);
        this.contactBookRepository = Objects.requireNonNull(contactBookRepository);
        this.conversationIdentifierProvider = Objects.requireNonNull(conversationIdentifierProvider);
        this.postedAtProvider = Objects.requireNonNull(postedAtProvider);
    }

    @Override
    public Conversation execute(final OpenANewConversationCommand command) {
        final ContactBook contactBook = this.contactBookRepository.getByOwner(new Owner(command.openedBy()));
        final List<ParticipantIdentifier> participantIdentifiersNotInContactBook = command.participantsIdentifier()
                .stream()
                .filter(participantIdentifier -> !contactBook.hasContact(participantIdentifier))
                .collect(Collectors.toUnmodifiableList());
        if (!participantIdentifiersNotInContactBook.isEmpty()) {
            throw new ParticipantsAreNotInContactBookException(participantIdentifiersNotInContactBook);
        }
        final ConversationIdentifier conversationIdentifier = conversationIdentifierProvider.generate(command.openedBy());
        final List<ParticipantIdentifier> participantIdentifiers = Stream.concat(
                Stream.of(command.openedBy().identifier()),
                command.participantsIdentifier().stream())
                .distinct().collect(Collectors.toUnmodifiableList());
        final Conversation conversation = new Conversation(conversationIdentifier,
                new Message(
                        new From(command.openedBy()),
                        postedAtProvider.now(),
                        command.content()),
                participantIdentifiers);
        this.conversationRepository.createConversation(conversation);
        return conversation;
    }

}
