package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

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
                        mock(ConversationIdentifier.class),
                        Collections.emptyList(),
                        Collections.emptyList()));

        // When && Then
        assertThatThrownBy(() -> new Conversations(new ParticipantIdentifier("Mario"), conversations))
                .isInstanceOf(IllegalStateException.class);
    }
}
