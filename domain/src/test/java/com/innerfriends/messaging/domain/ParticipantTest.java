package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ParticipantTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Participant.class).verify();
    }

    @Test
    public void should_fail_fast_when_conversation_event_is_not_added_participant() {
        assertThatThrownBy(() -> new Participant(new MessagePostedConversationEvent(new Message(new From("Mario"), new PostedAt(ZonedDateTime.now()), new Content("Hello !")))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Not expected event !");
    }
}