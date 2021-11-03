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
        final ContactIdentifier identifier = mock(ContactIdentifier.class);
        assertThat(new Owner(identifier).identifier()).isEqualTo(identifier);
    }

    @Test
    public void should_return_identifier_from_openedBy() {
        final ParticipantIdentifier identifier = new ParticipantIdentifier("Mario");
        assertThat(new Owner(new OpenedBy(identifier)).identifier()).isEqualTo(new ContactIdentifier("Mario"));
    }

}
