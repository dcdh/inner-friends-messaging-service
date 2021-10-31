package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Owner;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ListRecentContactsCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ListRecentContactsCommand.class).verify();
    }

    @Test
    public void should_identifier_return_owner() {
        // Given
        final Owner owner = mock(Owner.class);

        // When && Then
        assertThat(new ListRecentContactsCommand(owner, 0).identifier()).isEqualTo(owner);
    }
}
