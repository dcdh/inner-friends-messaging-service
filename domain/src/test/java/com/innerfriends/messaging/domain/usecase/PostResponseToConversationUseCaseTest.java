package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostResponseToConversationUseCaseTest {

    @Test
    public void should_post_response_to_conversation() {
        // Given
        final ConversationIdentifier conversationIdentifier = new TestConversationIdentifier("conversation");
        final Conversation conversation = spy(new Conversation(
                conversationIdentifier,
                List.of(
                        new Message(new From(new TestParticipantIdentifier("Peach")), buildPostedAt(2), new TestContent("I Mario How are you ?"))),
                Collections.emptyList()
        ));
        final ConversationRepository conversationRepository = mock(ConversationRepository.class);
        final PostResponseToConversationUseCase postResponseToConversationUseCase = new PostResponseToConversationUseCase(conversationRepository);
        doReturn(conversation).when(conversationRepository).getConversation(conversationIdentifier);

        // When && Then
        assertThat(postResponseToConversationUseCase.execute(new PostResponseToConversationCommand(
                new From(new TestParticipantIdentifier("Mario")),
                conversationIdentifier,
                new TestContent("I am fine thanks"),
                buildPostedAt(3)))).isEqualTo(new Message(new From(new TestParticipantIdentifier("Mario")), buildPostedAt(3), new TestContent("I am fine thanks")));
        verify(conversation, times(1)).post(new From(new TestParticipantIdentifier("Mario")), buildPostedAt(3), new TestContent("I am fine thanks"));
        assertThat(conversation.messages()).containsExactly(
                new Message(new From(new TestParticipantIdentifier("Peach")), buildPostedAt(2), new TestContent("I Mario How are you ?")),
                new Message(new From(new TestParticipantIdentifier("Mario")), buildPostedAt(3), new TestContent("I am fine thanks")));
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }
}
