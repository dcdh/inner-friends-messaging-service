package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class OpenANewConversationUseCaseTest {

    private ConversationRepository conversationRepository;
    private ContactBookRepository contactBookRepository;
    private ConversationIdentifierProvider conversationIdentifierProvider;

    @BeforeEach
    public void setup() {
        conversationRepository = mock(ConversationRepository.class);
        contactBookRepository = mock(ContactBookRepository.class);
        conversationIdentifierProvider = mock(ConversationIdentifierProvider.class);
    }

    @Test
    public void should_open_a_new_conversation() {
        // Given
        final Owner owner = new Owner(new ContactIdentifier("Mario"));
        final ContactBook contactBook = new ContactBook(owner, List.of(
                new Contact(new ContactIdentifier("Peach"), new AddedAt(ZonedDateTime.now())),
                new Contact(new ContactIdentifier("Luigi"), new AddedAt(ZonedDateTime.now()))),
                2l);

        final OpenANewConversationCommand openANewConversationCommand = new OpenANewConversationCommand(
                new OpenedBy(new ParticipantIdentifier("Mario")),
                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Luigi")),
                new OpenedAt(buildAt(2)),
                new TestContent("Hello Peach !")
        );
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);
        doReturn(new TestConversationIdentifier("conversationIdentifier")).when(conversationIdentifierProvider).generate(new OpenedBy(new ParticipantIdentifier("Mario")));
        final OpenANewConversationUseCase openANewConversationUseCase = new OpenANewConversationUseCase(conversationRepository,
                contactBookRepository, conversationIdentifierProvider);

        // When && Then
        assertThat(openANewConversationUseCase.execute(openANewConversationCommand))
                .isEqualTo(new Conversation(
                        new TestConversationIdentifier("conversationIdentifier"),
                        List.of(new Message(new From(new ParticipantIdentifier("Mario")), new PostedAt(buildAt(2)), new TestContent("Hello Peach !"))),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Luigi")),
                        0l
                ));
    }

    @Test
    public void should_fail_when_not_in_contact_book() {
        // Given
        final Owner owner = new Owner(new ContactIdentifier("Mario"));
        final ContactBook contactBook = new ContactBook(owner);
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);

        final OpenANewConversationCommand openANewConversationCommand = new OpenANewConversationCommand(
                new OpenedBy(new ParticipantIdentifier("Mario")),
                Collections.singletonList(new ParticipantIdentifier("Peach")),
                new OpenedAt(buildAt(2)),
                new TestContent("Hello Peach !")
        );
        final OpenANewConversationUseCase openANewConversationUseCase = new OpenANewConversationUseCase(conversationRepository,
                contactBookRepository, conversationIdentifierProvider);

        // When && Then
        assertThatThrownBy(() -> openANewConversationUseCase.execute(openANewConversationCommand))
                .isInstanceOf(ParticipantsAreNotInContactBookException.class)
                .hasFieldOrPropertyWithValue("participantIdentifiersNotInContactBook", List.of(new ParticipantIdentifier("Peach")));
    }

    private ZonedDateTime buildAt(final Integer day) {
        return ZonedDateTime.of(2021, 10, 31, 0, 0, 0, 0, ZoneId.of("Europe/Paris"));
    }

}
