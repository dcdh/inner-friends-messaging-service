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
public class StartConversationUseCaseTest {

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
    public void should_start_conversation() {
        // Given
        final Owner owner = new Owner(new TestParticipantIdentifier("Mario"));
        final ContactBook contactBook = new ContactBook(owner, List.of(new Contact(new ContactIdentifier(new TestParticipantIdentifier("Peach")), new AddedAt(ZonedDateTime.now()))));

        final StartConversationCommand startConversationCommand = new StartConversationCommand(
                new From(new TestParticipantIdentifier("Mario")),
                new To(new TestParticipantIdentifier("Peach")),
                new StartedAt(buildAt(2)),
                new TestContent("Hello Peach !")
        );
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);
        doReturn(new TestConversationIdentifier("conversationIdentifier")).when(conversationIdentifierProvider).generate(new From(new TestParticipantIdentifier("Mario")));
        final StartConversationUseCase startConversationUseCase = new StartConversationUseCase(conversationRepository,
                contactBookRepository, conversationIdentifierProvider);

        // When && Then
        assertThat(startConversationUseCase.execute(startConversationCommand))
                .isEqualTo(new Conversation(
                        new TestConversationIdentifier("conversationIdentifier"),
                        List.of(new Message(new From(new TestParticipantIdentifier("Mario")), new PostedAt(buildAt(2)), new TestContent("Hello Peach !"))),
                        List.of(new TestParticipantIdentifier("Mario"), new TestParticipantIdentifier("Peach"))
                ));
    }

    @Test
    public void should_throw_to_is_not_in_contact_book_exception_when_not_in_contact_book() {
        // Given
        final Owner owner = new Owner(new TestParticipantIdentifier("Mario"));
        final ContactBook contactBook = new ContactBook(owner, Collections.emptyList());
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);

        final StartConversationCommand startConversationCommand = new StartConversationCommand(
                new From(new TestParticipantIdentifier("Mario")),
                new To(new TestParticipantIdentifier("Peach")),
                new StartedAt(buildAt(2)),
                new TestContent("Hello Peach !")
        );
        final StartConversationUseCase startConversationUseCase = new StartConversationUseCase(conversationRepository,
                contactBookRepository, conversationIdentifierProvider);

        // When && Then
        assertThatThrownBy(() -> startConversationUseCase.execute(startConversationCommand))
                .isInstanceOf(ToIsNotInContactBookException.class);
    }

    private ZonedDateTime buildAt(final Integer day) {
        return ZonedDateTime.of(2021, 10, 31, 0, 0, 0, 0, ZoneId.of("Europe/Paris"));
    }

}
