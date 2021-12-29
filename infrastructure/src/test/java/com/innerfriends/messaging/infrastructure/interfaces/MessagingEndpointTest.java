package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.domain.usecase.*;
import com.innerfriends.messaging.infrastructure.usecase.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class MessagingEndpointTest {

    @InjectMock
    private ManagedListAllContactsUseCase managedListAllContactsUseCase;

    @InjectMock
    private ManagedListConversationsUseCase managedListConversationsUseCase;

    @InjectMock
    private ManagedListRecentContactsUseCase managedListRecentContactsUseCase;

    @InjectMock
    private ManagedPostNewMessageToConversationUseCase managedPostNewMessageToConversationUseCase;

    @InjectMock
    private ManagedOpenNewConversationUseCase managedOpenNewConversationUseCase;

    @InjectMock
    private ManagedListConversationEventUseCase managedListConversationEventUseCase;

    @Test
    @TestSecurity(user = "mario", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Mario")
    })
    public void should_list_mario_all_contacts() {
        // Given
        doReturn(new ListAllContactInContactBook(
                new ContactBook(
                        new Owner("Mario"),
                        buildCreatedAt(),
                        List.of(new Contact(new ContactIdentifier("Peach"), buildAddedAt(2)),
                                new Contact(new ContactIdentifier("Luigi"), buildAddedAt(3))),
                        2l)))
                .when(managedListAllContactsUseCase).execute(new ListAllContactsCommand(new Owner("Mario")));

        // When && Then
        given()
                .when()
                .get("/contacts/Mario")
                .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/contacts.json"))
                .body("owner", equalTo("Mario"))
                .body("createdAt", equalTo("2021-10-01T00:00:00+02:00[Europe/Paris]"))
                .body("contacts[0].contactIdentifier", equalTo("Luigi"))
                .body("contacts[0].addedAt", equalTo("2021-10-03T00:00:00+02:00"))
                .body("contacts[1].contactIdentifier", equalTo("Peach"))
                .body("contacts[1].addedAt", equalTo("2021-10-02T00:00:00+02:00"))
                .body("version", equalTo(2));
    }

    @Test
    @TestSecurity(user = "mario", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Mario")
    })
    public void should_list_mario_recent_contacts() {
        // Given
        doReturn(List.of(
                new ContactIdentifier("Peach"),
                new ContactIdentifier("Luigi")))
                .when(managedListRecentContactsUseCase).execute(new ListRecentContactsCommand(new Owner("Mario"), 2));

        // When && Then
        given()
                .param("nbOfContactToReturn", 2)
                .when()
                .post("/contacts/Mario/recent")
                .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/recent_contacts.json"))
                .body(equalTo("[\"Peach\",\"Luigi\"]"));
    }

    @Test
    @TestSecurity(user = "mario", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Mario")
    })
    public void should_mario_open_a_new_conversation() {
        // Given
        doReturn(new Conversation(
                new ConversationIdentifier("Mario-azerty"),
                new Message(new From("Mario"), buildPostedAt(1), new Content("Hello Peach !")),
                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario"))
        ))
                .when(managedOpenNewConversationUseCase)
                .execute(new OpenNewConversationCommand(
                        new OpenedBy(new ParticipantIdentifier("Mario")),
                        List.of(new ParticipantIdentifier("Peach")),
                        new Content("Hello Peach !")));

        // When && Then
        given()
                .param("to", "Peach")
                .param("content", "Hello Peach !")
                .when()
                .post("/conversations/openNewOne")
                .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/conversation.json"))
                .body("conversationIdentifier", equalTo("Mario-azerty"))
                .body("participantsIdentifier", contains("Peach", "Mario"))
                .body("events[0].conversationEventType", equalTo("STARTED"))
                .body("events[0].eventFrom", equalTo("Mario"))
                .body("events[0].eventAt", equalTo("2021-10-01T00:00:00+02:00"))
                .body("events[0].content", equalTo("Hello Peach !"))
                .body("events[0].participantsIdentifier", contains("Peach", "Mario"))
                .body("version", equalTo(0));
    }

    @Test
    @TestSecurity(user = "peach", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Peach")
    })
    public void should_list_peach_conversations() {
        // Given
        doReturn(List.of(new Conversation(
                new ConversationIdentifier("Peach-azerty"),
                List.of(
                        new StartedConversationEvent(new Message(new From("Peach"), buildPostedAt(1), new Content("Hi Mario How are you ?")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))),
                        new MessagePostedConversationEvent(new Message(new From("Mario"), buildPostedAt(2), new Content("I am fine thanks")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))),
                1l
        )))
                .when(managedListConversationsUseCase).execute(new ListConversationsCommand(new ParticipantIdentifier("Mario")));

        // When && Then
        given()
                .param("participantIdentifier", "Mario")
                .when()
                .post("/conversations/listConversations")
                .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/conversations.json"))
                .body("[0].conversationIdentifier", equalTo("Peach-azerty"))
                .body("[0].participantsIdentifier", contains("Mario", "Peach"))
                .body("[0].events[0].conversationEventType", equalTo("STARTED"))
                .body("[0].events[0].eventFrom", equalTo("Peach"))
                .body("[0].events[0].eventAt", equalTo("2021-10-01T00:00:00+02:00"))
                .body("[0].events[0].content", equalTo("Hi Mario How are you ?"))
                .body("[0].events[0].participantsIdentifier", contains("Mario", "Peach"))
                .body("[0].events[1].conversationEventType", equalTo("MESSAGE_POSTED"))
                .body("[0].events[1].eventFrom", equalTo("Mario"))
                .body("[0].events[1].eventAt", equalTo("2021-10-02T00:00:00+02:00"))
                .body("[0].events[1].content", equalTo("I am fine thanks"))
                .body("[0].events[1].participantsIdentifier", contains("Mario", "Peach"))
                .body("[0].version", equalTo(1));
    }

    @Test
    @TestSecurity(user = "mario", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Mario")
    })
    public void should_mario_post_a_new_message_to_conversation() {
        // Given
        doReturn(new Conversation(
                new ConversationIdentifier("Peach-azerty"),
                List.of(
                        new StartedConversationEvent(new Message(new From("Peach"), buildPostedAt(1), new Content("Hi Mario How are you ?")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))),
                        new MessagePostedConversationEvent(new Message(new From("Mario"), buildPostedAt(2), new Content("I am fine thanks")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))),
                1l
        ))
                .when(managedPostNewMessageToConversationUseCase).execute(new PostNewMessageToConversationCommand(
                new From("Mario"), new ConversationIdentifier("Peach-azerty"), new Content("I am fine thanks")));

        // When && Then
        given()
                .param("conversationIdentifier", "Peach-azerty")
                .param("content", "I am fine thanks")
                .when()
                .post("/conversations/postNewMessage")
                .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "mario", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Mario")
    })
    public void should_list_messages_in_mario_conversation() {
        // Given
        doReturn(List.of(
                new StartedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))))
                .when(managedListConversationEventUseCase)
                .execute(new ListConversationEventCommand(new ConversationIdentifier("Mario-azerty")));

        // When && Then
        given()
                .when()
                .get("/conversations/Mario-azerty/events")
                .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/events_in_conversation.json"))
                .body("[0].conversationEventType", equalTo("STARTED"))
                .body("[0].eventFrom", equalTo("Peach"))
                .body("[0].eventAt", equalTo("2021-10-02T00:00:00+02:00"))
                .body("[0].content", equalTo("Hi Mario How are you ?"))
                .body("[0].participantsIdentifier", contains("Mario", "Peach"));
    }

    @Test
    @TestSecurity(user = "mario", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Mario")
    })
    public void should_handle_no_contact_book_found() {
        // Given
        doThrow(new NoContactBookFoundException())
                .when(managedListAllContactsUseCase).execute(new ListAllContactsCommand(new Owner("Mario")));

        // When && Then
        given()
                .when()
                .get("/contacts/Mario")
                .then()
                .log().all()
                .statusCode(404)
                .body(equalTo("No contact book found"));
    }

    @Test
    @TestSecurity(user = "mario", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Mario")
    })
    public void should_handle_participants_are_not_in_contact_book() {
        // Given
        final List<ParticipantIdentifier> participantIdentifiersNotInContactBook = List.of(new ParticipantIdentifier("Bowser"));
        doThrow(new ParticipantsAreNotInContactBookException(participantIdentifiersNotInContactBook))
                .when(managedOpenNewConversationUseCase)
                .execute(new OpenNewConversationCommand(
                        new OpenedBy(new ParticipantIdentifier("Mario")),
                        List.of(new ParticipantIdentifier("Bowser")),
                        new Content("Hello Bowser I am gonna kick your ass !")));

        // When && Then
        given()
                .param("to", "Bowser")
                .param("content", "Hello Bowser I am gonna kick your ass !")
                .when()
                .post("/conversations/openNewOne")
                .then()
                .log().all()
                .statusCode(406)
                .body(equalTo("Your are not in the contact book !"));
    }

    @Test
    @TestSecurity(user = "mario", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Mario")
    })
    public void should_handle_unknown_conversation() {
        // Given
        final ConversationIdentifier unknownConversationIdentifier = new ConversationIdentifier("Bowser-azerty");
        doThrow(new UnknownConversationException(unknownConversationIdentifier))
                .when(managedPostNewMessageToConversationUseCase).execute(new PostNewMessageToConversationCommand(
                new From("Mario"), new ConversationIdentifier("Bowser-azerty"), new Content("Hello Bowser I am gonna kick your ass !")));

        // When && Then
        given()
                .param("conversationIdentifier", "Bowser-azerty")
                .param("content", "Hello Bowser I am gonna kick your ass !")
                .when()
                .post("/conversations/postNewMessage")
                .then()
                .log().all()
                .statusCode(404)
                .body(equalTo("The conversation Bowser-azerty does not exists !"));
    }

    @Test
    @TestSecurity(user = "bowser", roles = "friend")
    @JwtSecurity(claims = {
            @Claim(key = "pseudo", value = "Bowser")
    })
    public void should_handle_not_a_participant() {
        // Given
        final ConversationIdentifier conversationIdentifier = new ConversationIdentifier("Mario-azerty");
        final From from = new From("Bowser");
        doThrow(new YouAreNotAParticipantException(conversationIdentifier, from))
                .when(managedPostNewMessageToConversationUseCase).execute(new PostNewMessageToConversationCommand(
                new From("Bowser"), new ConversationIdentifier("Mario-azerty"), new Content("Ha ha ha Mario !")));

        // When && Then
        given()
                .param("conversationIdentifier", "Mario-azerty")
                .param("content", "Ha ha ha Mario !")
                .when()
                .post("/conversations/postNewMessage")
                .then()
                .log().all()
                .statusCode(400)
                .body(equalTo("You are not in the list of participant for this conversation !"));
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

    private CreatedAt buildCreatedAt() {
        return new CreatedAt(
                ZonedDateTime.of(2021, 10, 1, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
