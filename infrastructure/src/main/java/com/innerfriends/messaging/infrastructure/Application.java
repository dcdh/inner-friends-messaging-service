package com.innerfriends.messaging.infrastructure;

import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.domain.usecase.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.ZonedDateTime;
import java.util.UUID;

public class Application {

    @Produces
    @ApplicationScoped
    public AddedAtProvider addedAtProviderProducer() {
        return () -> new AddedAt(ZonedDateTime.now());
    }

    @Produces
    @ApplicationScoped
    public ConversationIdentifierProvider conversationIdentifierProvider() {
        return new ConversationIdentifierProvider() {
            @Override
            public ConversationIdentifier generate(final OpenedBy openedBy) {
                return new ConversationIdentifier(openedBy.identifier().identifier() + "_" + UUID.randomUUID());
            }
        };
    }

    @Produces
    @ApplicationScoped
    public PostedAtProvider postedAtProvider() {
        return new PostedAtProvider() {
            @Override
            public PostedAt now() {
                return new PostedAt(ZonedDateTime.now());
            }
        };
    }

    @Produces
    @ApplicationScoped
    public CreatedAtProvider createdAtProvider() {
        return new CreatedAtProvider() {
            @Override
            public CreatedAt now() {
                return new CreatedAt(ZonedDateTime.now());
            }
        };
    }

    @Produces
    @ApplicationScoped
    public AddContactIntoContactBookUseCase addContactIntoContactBookUseCaseProducer(final ContactBookRepository contactBookRepository,
                                                                                     final AddedAtProvider addedAtProvider) {
        return new AddContactIntoContactBookUseCase(contactBookRepository, addedAtProvider);
    }

    @Produces
    @ApplicationScoped
    public CreateContactBookUseCase createContactBookUseCaseProducer(final ContactBookRepository contactBookRepository,
                                                                     final CreatedAtProvider createdAtProvider) {
        return new CreateContactBookUseCase(contactBookRepository, createdAtProvider);
    }

    @Produces
    @ApplicationScoped
    public ListAllContactsUseCase listAllContactsUseCaseProducer(final ContactBookRepository contactBookRepository) {
        return new ListAllContactsUseCase(contactBookRepository);
    }

    @Produces
    @ApplicationScoped
    public ListConversationsUseCase listConversationsUseCaseProducer(final ConversationRepository conversationRepository) {
        return new ListConversationsUseCase(conversationRepository);
    }

    @Produces
    @ApplicationScoped
    public ListConversationEventUseCase listConversationEventUseCaseProducer(final ConversationRepository conversationRepository) {
        return new ListConversationEventUseCase(conversationRepository);
    }

    @Produces
    @ApplicationScoped
    public ListRecentContactsUseCase listRecentContactsUseCaseProducer(final ContactBookRepository contactBookRepository) {
        return new ListRecentContactsUseCase(contactBookRepository);
    }

    @Produces
    @ApplicationScoped
    public PostNewMessageToConversationUseCase postNewMessageToConversationUseCase(final ConversationRepository conversationRepository,
                                                                                   final PostedAtProvider postedAtProvider) {
        return new PostNewMessageToConversationUseCase(conversationRepository, postedAtProvider);
    }

    @Produces
    @ApplicationScoped
    public OpenNewConversationUseCase startConversationUseCaseProducer(final ConversationRepository conversationRepository,
                                                                       final ContactBookRepository contactBookRepository,
                                                                       final ConversationIdentifierProvider conversationIdentifierProvider,
                                                                       final PostedAtProvider postedAtProvider) {
        return new OpenNewConversationUseCase(conversationRepository, contactBookRepository, conversationIdentifierProvider, postedAtProvider);
    }

    @Produces
    @ApplicationScoped
    public AddParticipantIntoConversationUseCase addAParticipantIntoConversationUseCaseProducer(final ConversationRepository conversationRepository,
                                                                                                final AddedAtProvider addedAtProvider) {
        return new AddParticipantIntoConversationUseCase(conversationRepository, addedAtProvider);
    }

}
