package com.innerfriends.messaging.infrastructure.postgres;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class ConversationEventEntityTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ConversationEventEntity.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}
