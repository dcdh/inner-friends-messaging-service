package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ContactBookTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ContactBook.class).verify();
    }

}
