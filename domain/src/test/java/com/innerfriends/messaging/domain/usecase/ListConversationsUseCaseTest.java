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
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(1)),
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Luigi"), buildAddedAt(1)),
                                new MessagePostedConversationEvent(new Message(new From("Mario"), buildPostedAt(1), new Content("Hello Luigi"))),
                                new MessagePostedConversationEvent(new Message(new From("Luigi"), buildPostedAt(2), new Content("Hi Mario !")))),
                        1l
                ),
                new Conversation(
                        conversationIdentifier2,
                        List.of(
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(4)),
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Bowser"), buildAddedAt(4)),
                                new MessagePostedConversationEvent(new Message(new From("Bowser"), buildPostedAt(4), new Content("Mario you should run as fast as you can !")))),
                        0l
                ),
                new Conversation(
                        conversationIdentifier3,
                        List.of(
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(1)),
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Peach"), buildAddedAt(1)),
                                new MessagePostedConversationEvent(new Message(new From("Peach"), buildPostedAt(1), new Content("Hi Mario How are you ?")))),
                        0l
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
                        List.of(new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(4)),
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Bowser"), buildAddedAt(4)),
                                new MessagePostedConversationEvent(new Message(new From("Bowser"), buildPostedAt(4), new Content("Mario you should run as fast as you can !")))),
                        0l
                ),
                new Conversation(
                        conversationIdentifier1,
                        List.of(
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(1)),
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Luigi"), buildAddedAt(1)),
                                new MessagePostedConversationEvent(new Message(new From("Mario"), buildPostedAt(1), new Content("Hello Luigi"))),
                                new MessagePostedConversationEvent(new Message(new From("Luigi"), buildPostedAt(2), new Content("Hi Mario !")))),
                        1l
                ),
                new Conversation(
                        conversationIdentifier3,
                        List.of(
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(1)),
                                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Peach"), buildAddedAt(1)),
                                new MessagePostedConversationEvent(new Message(new From("Peach"), buildPostedAt(1), new Content("Hi Mario How are you ?")))),
                        0l
                )
        );
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
