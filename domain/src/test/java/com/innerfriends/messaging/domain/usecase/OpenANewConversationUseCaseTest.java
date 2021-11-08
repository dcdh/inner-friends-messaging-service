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
    private PostedAtProvider postedAtProvider;
    private OpenANewConversationUseCase openANewConversationUseCase;

    @BeforeEach
    public void setup() {
        conversationRepository = mock(ConversationRepository.class);
        contactBookRepository = mock(ContactBookRepository.class);
        conversationIdentifierProvider = mock(ConversationIdentifierProvider.class);
        postedAtProvider = mock(PostedAtProvider.class);
        openANewConversationUseCase = new OpenANewConversationUseCase(conversationRepository,
                contactBookRepository, conversationIdentifierProvider, postedAtProvider);
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
                new Content("Hello Peach !")
        );
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);
        doReturn(new ConversationIdentifier("conversationIdentifier")).when(conversationIdentifierProvider).generate(new OpenedBy(new ParticipantIdentifier("Mario")));
        doReturn(buildPostedAt(2)).when(postedAtProvider).now();

        // When && Then
        assertThat(openANewConversationUseCase.execute(openANewConversationCommand))
                .isEqualTo(new Conversation(
                        new ConversationIdentifier("conversationIdentifier"),
                        List.of(new Message(new From("Mario"), buildPostedAt(2), new Content("Hello Peach !"))),
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
                new Content("Hello Peach !")
        );

        // When && Then
        assertThatThrownBy(() -> openANewConversationUseCase.execute(openANewConversationCommand))
                .isInstanceOf(ParticipantsAreNotInContactBookException.class)
                .hasFieldOrPropertyWithValue("participantIdentifiersNotInContactBook", List.of(new ParticipantIdentifier("Peach")));
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
