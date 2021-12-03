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
public class PostNewMessageToConversationUseCaseTest {

    @Test
    public void should_post_response_to_conversation() {
        // Given
        final ConversationIdentifier conversationIdentifier = new ConversationIdentifier("conversation");
        final Conversation conversation = new Conversation(
                conversationIdentifier,
                List.of(new ParticipantAddedConversationEvent(new ParticipantIdentifier("Peach"), buildAddedAt(2)),
                        new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(2)),
                        new MessagePostedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")))),
                0l
        );
        final ConversationRepository conversationRepository = mock(ConversationRepository.class);
        doReturn(conversation).when(conversationRepository).getConversation(conversationIdentifier);
        final PostedAtProvider postedAtProvider = mock(PostedAtProvider.class);
        doReturn(buildPostedAt(3)).when(postedAtProvider).now();
        final PostNewMessageToConversationUseCase postNewMessageToConversationUseCase = new PostNewMessageToConversationUseCase(conversationRepository, postedAtProvider);

        // When && Then
        final Conversation expectedConversation = new Conversation(
                conversationIdentifier,
                List.of(
                        new ParticipantAddedConversationEvent(new ParticipantIdentifier("Peach"), buildAddedAt(2)),
                        new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(2)),
                        new MessagePostedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?"))),
                        new MessagePostedConversationEvent(new Message(new From("Mario"), buildPostedAt(3), new Content("I am fine thanks")))),
                1l
        );
        assertThat(postNewMessageToConversationUseCase.execute(new PostNewMessageToConversationCommand(
                new From("Mario"),
                conversationIdentifier,
                new Content("I am fine thanks")))).isEqualTo(expectedConversation);
        assertThat(conversation.messages()).containsExactly(
                new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                new Message(new From("Mario"), buildPostedAt(3), new Content("I am fine thanks")));
        assertThat(conversation.lastMessage()).isEqualTo(new Message(new From("Mario"), buildPostedAt(3), new Content("I am fine thanks")));
        assertThat(conversation.lastInteractionAt()).isEqualTo(new LastInteractionAt(buildPostedAt(3)));
        verify(conversationRepository, times(1)).saveConversation(expectedConversation);
    }

    @Test
    public void should_fail_when_the_one_responding_is_not_a_participant_of_the_discussion() {
        // Given
        final ConversationIdentifier conversationIdentifier = new ConversationIdentifier("conversation");
        final Conversation conversation = new Conversation(
                conversationIdentifier,
                List.of(
                        new ParticipantAddedConversationEvent(new ParticipantIdentifier("Peach"), buildAddedAt(2)),
                        new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), buildAddedAt(2)),
                        new MessagePostedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")))),
                0l
        );
        final ConversationRepository conversationRepository = mock(ConversationRepository.class);
        doReturn(conversation).when(conversationRepository).getConversation(conversationIdentifier);
        final PostedAtProvider postedAtProvider = mock(PostedAtProvider.class);
        doReturn(buildPostedAt(3)).when(postedAtProvider).now();
        final PostNewMessageToConversationUseCase postNewMessageToConversationUseCase = new PostNewMessageToConversationUseCase(conversationRepository, postedAtProvider);

        // When && Then
        assertThatThrownBy(() -> postNewMessageToConversationUseCase.execute(new PostNewMessageToConversationCommand(
                new From("Luigi"),
                conversationIdentifier,
                new Content("I am jealous :P"))))
                .isInstanceOf(YouAreNotAParticipantException.class)
                .hasFieldOrPropertyWithValue("conversationIdentifier", conversationIdentifier)
                .hasFieldOrPropertyWithValue("from", new From("Luigi"));
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
