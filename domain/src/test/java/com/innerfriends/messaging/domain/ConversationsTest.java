package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ConversationsTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Conversations.class).verify();
    }

    @Test
    public void should_fail_fast_when_participant_not_in_conversation() {
        // Given
        final List<Conversation> conversations = List.of(
                new Conversation(
                        new ConversationIdentifier("conversation"),
                        new Message(new From("Peach"), new PostedAt(ZonedDateTime.now()), new Content("Hi Mario How are you ?")),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))));

        // When && Then
        assertThatThrownBy(() -> new Conversations(new ParticipantIdentifier("Luigi"), conversations))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void should_handle_empty_conversations() {
        assertThat(new Conversations(mock(ParticipantIdentifier.class), Collections.emptyList()).listByLastInteraction())
                .isEmpty();
    }
}
