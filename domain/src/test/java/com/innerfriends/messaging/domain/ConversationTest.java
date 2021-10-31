package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ConversationTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Conversation.class).verify();
    }

}
