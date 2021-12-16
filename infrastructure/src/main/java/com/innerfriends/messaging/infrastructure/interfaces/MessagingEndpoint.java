package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.domain.usecase.*;
import com.innerfriends.messaging.infrastructure.usecase.*;
import io.quarkus.security.Authenticated;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Path("/")
@RequestScoped
@Authenticated
public class MessagingEndpoint {

    private final ManagedListAllContactsUseCase managedListAllContactsUseCase;
    private final ManagedListConversationsUseCase managedListConversationsUseCase;
    private final ManagedListRecentContactsUseCase managedListRecentContactsUseCase;
    private final ManagedPostNewMessageToConversationUseCase managedPostNewMessageToConversationUseCase;
    private final ManagedOpenNewConversationUseCase managedOpenNewConversationUseCase;
    private final ManagedListConversationEventUseCase managedListConversationEventUseCase;

    public MessagingEndpoint(final ManagedListAllContactsUseCase managedListAllContactsUseCase,
                             final ManagedListConversationsUseCase managedListConversationsUseCase,
                             final ManagedListRecentContactsUseCase managedListRecentContactsUseCase,
                             final ManagedPostNewMessageToConversationUseCase managedPostNewMessageToConversationUseCase,
                             final ManagedOpenNewConversationUseCase managedOpenNewConversationUseCase,
                             final ManagedListConversationEventUseCase managedListConversationEventUseCase) {
        this.managedListAllContactsUseCase = Objects.requireNonNull(managedListAllContactsUseCase);
        this.managedListConversationsUseCase = Objects.requireNonNull(managedListConversationsUseCase);
        this.managedListRecentContactsUseCase = Objects.requireNonNull(managedListRecentContactsUseCase);
        this.managedPostNewMessageToConversationUseCase = Objects.requireNonNull(managedPostNewMessageToConversationUseCase);
        this.managedOpenNewConversationUseCase = Objects.requireNonNull(managedOpenNewConversationUseCase);
        this.managedListConversationEventUseCase = Objects.requireNonNull(managedListConversationEventUseCase);
    }

    @GET
    @Path("/contacts/{owner}")
    public ContactBookDTO listAllContacts(@PathParam("owner") final String owner) {
        return new ContactBookDTO(managedListAllContactsUseCase.execute(new ListAllContactsCommand(new Owner(owner))));
    }

    @POST
    @Path("/contacts/{owner}/recent")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public List<String> listRecentContacts(@PathParam("owner") final String owner,
                                           @FormParam("nbOfContactToReturn") final Integer nbOfContactToReturn) {
        return managedListRecentContactsUseCase.execute(new ListRecentContactsCommand(
                new Owner(owner),
                nbOfContactToReturn
        ))
                .stream()
                .map(ContactIdentifier::identifier)
                .collect(Collectors.toList());
    }

    @POST
    @Path("/conversations/openNewOne")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ConversationDTO openNewConversation(@FormParam("openedBy") final String openedBy,
                                               @FormParam("to") final String to,
                                               @FormParam("content") final String content) {
        return new ConversationDTO(managedOpenNewConversationUseCase.execute(new OpenNewConversationCommand(
                new OpenedBy(new ParticipantIdentifier(openedBy)),
                List.of(new ParticipantIdentifier(to)),
                new Content(content)
        )));
    }

    @POST
    @Path("/conversations/listConversations")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public List<ConversationDTO> listConversations(@FormParam("participantIdentifier") final String participantIdentifier) {
        return managedListConversationsUseCase.execute(new ListConversationsCommand(new ParticipantIdentifier(participantIdentifier)))
                .stream()
                .map(ConversationDTO::new)
                .collect(Collectors.toList());
    }

    @POST
    @Path("/conversations/postNewMessage")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void postNewMessageToConversation(@FormParam("conversationIdentifier") final String conversationIdentifier,
                                             @FormParam("from") final String from,
                                             @FormParam("content") final String content) {
        managedPostNewMessageToConversationUseCase.execute(new PostNewMessageToConversationCommand(
                new From(from),
                new ConversationIdentifier(conversationIdentifier),
                new Content(content)
        ));
    }

    @GET
    @Path("/conversations/{conversationIdentifier}/events")
    public List<ConversationEventDTO> listEventsInConversation(@PathParam("conversationIdentifier") final String conversationIdentifier) {
        return managedListConversationEventUseCase.execute(new ListConversationEventCommand(
                new ConversationIdentifier(conversationIdentifier)))
                .stream().map(ConversationEventDTO::new)
                .collect(Collectors.toList());
    }

}
