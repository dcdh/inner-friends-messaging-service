package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddParticipantIntoConversationUseCaseTest {

    private ConversationRepository conversationRepository;
    private AddedAtProvider addedAtProvider;
    private AddParticipantIntoConversationUseCase addParticipantIntoConversationUseCase;

    @BeforeEach
    public void setup() {
        conversationRepository = mock(ConversationRepository.class);
        addedAtProvider = mock(AddedAtProvider.class);
        addParticipantIntoConversationUseCase = new AddParticipantIntoConversationUseCase(conversationRepository, addedAtProvider);
    }

    @Test
    public void should_add_participant_into_conversation() {
        // Given
        final ConversationIdentifier conversationIdentifier = new ConversationIdentifier("conversation");
        final Conversation conversation = new Conversation(
                conversationIdentifier,
                List.of(
                        new StartedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario")))),
                0l
        );
        doReturn(conversation).when(conversationRepository).getConversation(conversationIdentifier);
        doReturn(buildAddedAt(3)).when(addedAtProvider).now();

        // When && Then
        final Conversation expectedConversation = new Conversation(
                conversationIdentifier,
                List.of(
                        new StartedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario"))),
                        new ParticipantAddedConversationEvent(new ParticipantIdentifier("Luigi"), buildAddedAt(3))),
                1l
        );
        assertThat(addParticipantIntoConversationUseCase.execute(new AddParticipantIntoConversationCommand(conversationIdentifier, new ParticipantIdentifier("Luigi"))))
                .isEqualTo(expectedConversation);
        assertThat(conversation.events()).containsExactly(
                new StartedConversationEvent(new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                        List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario"))),
                new ParticipantAddedConversationEvent(new ParticipantIdentifier("Luigi"), buildAddedAt(3)));
        assertThat(conversation.lastInteractionAt()).isEqualTo(new LastInteractionAt(buildAddedAt(3)));
        assertThat(conversation.lastAddedParticipant()).isEqualTo(new Participant(new ParticipantIdentifier("Luigi"), buildAddedAt(3)));
        verify(conversationRepository, times(1)).saveConversation(expectedConversation);
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