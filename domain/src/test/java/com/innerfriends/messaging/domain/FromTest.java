package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class FromTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(From.class).verify();
    }

    @Test
    public void should_return_participant_identifier() {
        // Given
        final ParticipantIdentifier participantIdentifier = mock(ParticipantIdentifier.class);

        // When && Then
        assertThat(new From(participantIdentifier).identifier()).isEqualTo(participantIdentifier);
    }

}
