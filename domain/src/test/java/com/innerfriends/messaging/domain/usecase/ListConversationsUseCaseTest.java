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
public class ListConversationsUseCaseTest {

    @Test
    public void should_list_conversations() {
        // Given
        final ConversationIdentifier conversationIdentifier1 = mock(ConversationIdentifier.class);
        final ConversationIdentifier conversationIdentifier2 = mock(ConversationIdentifier.class);
        final ConversationIdentifier conversationIdentifier3 = mock(ConversationIdentifier.class);

        final List<Conversation> conversations = List.of(
                new Conversation(
                        conversationIdentifier1,
                        List.of(
                                new Message(new From(new ParticipantIdentifier("Mario")), buildPostedAt(1), new TestContent("Hello Luigi")),
                                new Message(new From(new ParticipantIdentifier("Luigi")), buildPostedAt(2), new TestContent("Hi Mario !"))),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Luigi"))
                ),
                new Conversation(
                        conversationIdentifier2,
                        List.of(
                                new Message(new From(new ParticipantIdentifier("Bowser")), buildPostedAt(4), new TestContent("Mario you should run as fast as you can !"))),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Bowser"))
                ),
                new Conversation(
                        conversationIdentifier3,
                        List.of(
                                new Message(new From(new ParticipantIdentifier("Peach")), buildPostedAt(1), new TestContent("I Mario How are you ?"))),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))
                )
        );
        final ParticipantIdentifier participantIdentifier = new ParticipantIdentifier("Mario");
        final ConversationRepository conversationRepository = mock(ConversationRepository.class);
        doReturn(conversations).when(conversationRepository).getConversationsForParticipant(participantIdentifier);
        final ListConversationsCommand listConversationsCommand = new ListConversationsCommand(participantIdentifier);
        final ListConversationsUseCase listConversationsUseCase = new ListConversationsUseCase(conversationRepository);

        // When && Then
        assertThat(listConversationsUseCase.execute(listConversationsCommand)).containsExactly(
                new Conversation(
                        conversationIdentifier2,
                        List.of(
                                new Message(new From(new ParticipantIdentifier("Bowser")), buildPostedAt(4), new TestContent("Mario you should run as fast as you can !"))),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Bowser"))
                ),
                new Conversation(
                        conversationIdentifier1,
                        List.of(
                                new Message(new From(new ParticipantIdentifier("Mario")), buildPostedAt(1), new TestContent("Hello Luigi")),
                                new Message(new From(new ParticipantIdentifier("Luigi")), buildPostedAt(2), new TestContent("Hi Mario !"))),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Luigi"))
                ),
                new Conversation(
                        conversationIdentifier3,
                        List.of(
                                new Message(new From(new ParticipantIdentifier("Peach")), buildPostedAt(1), new TestContent("I Mario How are you ?"))),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))
                )
        );
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
