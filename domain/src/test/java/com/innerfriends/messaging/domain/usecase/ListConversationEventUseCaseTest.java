package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListConversationEventUseCaseTest {

    @Test
    public void should_list_messages_in_conversation() {
        // Given
        final ConversationRepository conversationRepository = mock(ConversationRepository.class);

        final ListConversationEventUseCase listConversationEventUseCase = new ListConversationEventUseCase(conversationRepository);
        final Conversation conversation = new Conversation(
                mock(ConversationIdentifier.class),
                List.of(
                        new StartedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))),
                        new MessagePostedConversationEvent(new Message(new From("Mario"), buildPostedAt(3), new Content("I am fine thanks")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))),
                1l
        );
        final ConversationIdentifier conversationIdentifier = mock(ConversationIdentifier.class);
        doReturn(conversation).when(conversationRepository).getConversation(conversationIdentifier);
        final ListConversationEventCommand listConversationEventCommand = new ListConversationEventCommand(
                conversationIdentifier);

        // When && Then
        assertThat(listConversationEventUseCase.execute(listConversationEventCommand))
                .containsExactly(
                        new StartedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))),
                        new MessagePostedConversationEvent(new Message(new From("Mario"), buildPostedAt(3), new Content("I am fine thanks")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))));
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
