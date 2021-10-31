package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ParticipantIdentifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ListConversationsCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ListConversationsCommand.class).verify();
    }

    @Test
    public void should_identifier_return_participant_identifier() {
        // Given
        final ParticipantIdentifier participantIdentifier = mock(ParticipantIdentifier.class);

        // When && Then
        assertThat(new ListConversationsCommand(participantIdentifier).identifier()).isEqualTo(participantIdentifier);
    }
}
