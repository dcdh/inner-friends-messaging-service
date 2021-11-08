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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListMessagesInConversationUseCaseTest {

    @Test
    public void should_list_messages_in_conversation() {
        // Given
        final ConversationRepository conversationRepository = mock(ConversationRepository.class);

        final ListMessagesInConversationUseCase listMessagesInConversationUseCase = new ListMessagesInConversationUseCase(conversationRepository);
        final Conversation conversation = new Conversation(
                mock(ConversationIdentifier.class),
                List.of(
                        new Message(new From("Mario"), buildPostedAt(3), new Content("I am fine thanks")),
                        new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?"))),
                Collections.emptyList(),
                1l
        );
        final ConversationIdentifier conversationIdentifier = mock(ConversationIdentifier.class);
        doReturn(conversation).when(conversationRepository).getConversation(conversationIdentifier);
        final ListMessagesInConversationCommand listMessagesInConversationCommand = new ListMessagesInConversationCommand(
                conversationIdentifier);

        // When && Then
        assertThat(listMessagesInConversationUseCase.execute(listMessagesInConversationCommand))
                .containsExactly(
                        new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                        new Message(new From("Mario"), buildPostedAt(3), new Content("I am fine thanks")));
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }
}
