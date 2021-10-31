package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactIdentifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CreateContactBookCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(CreateContactBookCommand.class).verify();
    }

    @Test
    public void should_identifier_return_contact_identifier() {
        // Given
        final ContactIdentifier contactIdentifier = mock(ContactIdentifier.class);

        // When && Then
        assertThat(new CreateContactBookCommand(contactIdentifier).identifier()).isEqualTo(contactIdentifier);
    }
}
