package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OwnerTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Owner.class).verify();
    }

    @Test
    public void should_return_identifier() {
        final ParticipantIdentifier identifier = mock(ParticipantIdentifier.class);
        assertThat(new Owner(identifier).identifier()).isEqualTo(identifier);
    }

    @Test
    public void should_return_identifier_from_contact_identifier() {
        final ParticipantIdentifier identifier = mock(ParticipantIdentifier.class);
        assertThat(new Owner(new ContactIdentifier(identifier)).identifier()).isEqualTo(identifier);
    }

    @Test
    public void should_return_identifier_from_openedBy() {
        final ParticipantIdentifier identifier = mock(ParticipantIdentifier.class);
        assertThat(new Owner(new OpenedBy(identifier)).identifier()).isEqualTo(identifier);
    }

}
