package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostResponseToConversationUseCaseTest {

    @Test
    public void should_post_response_to_conversation() {
        // Given
        final ConversationIdentifier conversationIdentifier = new TestConversationIdentifier("conversation");
        final Conversation conversation = new Conversation(
                conversationIdentifier,
                List.of(
                        new Message(new From(new ParticipantIdentifier("Peach")), buildPostedAt(2), new TestContent("I Mario How are you ?"))),
                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario")),
                0l
        );
        final ConversationRepository conversationRepository = mock(ConversationRepository.class);
        doReturn(conversation).when(conversationRepository).getConversation(conversationIdentifier);
        final PostResponseToConversationUseCase postResponseToConversationUseCase = new PostResponseToConversationUseCase(conversationRepository);

        // When && Then
        final Conversation expectedConversation = new Conversation(
                conversationIdentifier,
                List.of(
                        new Message(new From(new ParticipantIdentifier("Peach")), buildPostedAt(2), new TestContent("I Mario How are you ?")),
                        new Message(new From(new ParticipantIdentifier("Mario")), buildPostedAt(3), new TestContent("I am fine thanks"))),
                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario")),
                1l
        );
        assertThat(postResponseToConversationUseCase.execute(new PostResponseToConversationCommand(
                new From(new ParticipantIdentifier("Mario")),
                conversationIdentifier,
                new TestContent("I am fine thanks"),
                buildPostedAt(3)))).isEqualTo(expectedConversation);
        assertThat(conversation.messages()).containsExactly(
                new Message(new From(new ParticipantIdentifier("Peach")), buildPostedAt(2), new TestContent("I Mario How are you ?")),
                new Message(new From(new ParticipantIdentifier("Mario")), buildPostedAt(3), new TestContent("I am fine thanks")));
        assertThat(conversation.lastMessage()).isEqualTo(new Message(new From(new ParticipantIdentifier("Mario")), buildPostedAt(3), new TestContent("I am fine thanks")));
        assertThat(conversation.lastInteractionAt()).isEqualTo(new LastInteractionAt(buildPostedAt(3)));
        verify(conversationRepository, times(1)).saveConversation(expectedConversation);
    }

    @Test
    public void should_fail_when_the_one_responding_is_not_a_participant_of_the_discussion() {
        // Given
        final ConversationIdentifier conversationIdentifier = new TestConversationIdentifier("conversation");
        final Conversation conversation = new Conversation(
                conversationIdentifier,
                List.of(
                        new Message(new From(new ParticipantIdentifier("Peach")), buildPostedAt(2), new TestContent("I Mario How are you ?"))),
                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario")),
                0l
        );
        final ConversationRepository conversationRepository = mock(ConversationRepository.class);
        doReturn(conversation).when(conversationRepository).getConversation(conversationIdentifier);
        final PostResponseToConversationUseCase postResponseToConversationUseCase = new PostResponseToConversationUseCase(conversationRepository);

        // When && Then
        assertThatThrownBy(() -> postResponseToConversationUseCase.execute(new PostResponseToConversationCommand(
                new From(new ParticipantIdentifier("Luigi")),
                conversationIdentifier,
                new TestContent("I am jealous :P"),
                buildPostedAt(3))))
                .isInstanceOf(YouAreNotAParticipantException.class)
                .hasFieldOrPropertyWithValue("conversationIdentifier", conversationIdentifier)
                .hasFieldOrPropertyWithValue("from", new From(new ParticipantIdentifier("Luigi")));
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }
}
