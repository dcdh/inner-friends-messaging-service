package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ContactBookTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ContactBook.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void should_initialize_version_to_zero() {
        // Given
        final Owner owner = mock(Owner.class);
        final CreatedAt createdAt = mock(CreatedAt.class);

        // When && Then
        final ContactBook contactBook = new ContactBook(owner, createdAt, Collections.emptyList());
        assertThat(contactBook).isEqualTo(new ContactBook(owner, createdAt, Collections.emptyList(), 0l));
        assertThat(contactBook.version()).isEqualTo(0l);
    }

}
