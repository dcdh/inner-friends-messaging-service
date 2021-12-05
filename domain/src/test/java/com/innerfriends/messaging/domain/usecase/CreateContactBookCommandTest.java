package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Owner;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateContactBookCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(CreateContactBookCommand.class).verify();
    }

    @Test
    public void should_identifier_return_contact_identifier() {
        assertThat(new CreateContactBookCommand(new Owner("Mario")).identifier()).isEqualTo(new Owner("Mario"));
    }
}
