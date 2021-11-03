package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ParticipantIdentifierTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ParticipantIdentifier.class).verify();
    }

}
