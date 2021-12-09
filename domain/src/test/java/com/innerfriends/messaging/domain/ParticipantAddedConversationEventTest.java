package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ParticipantAddedConversationEventTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ParticipantAddedConversationEvent.class).verify();
    }

    @Test
    public void should_return_message_posted_type() {
        assertThat(new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), new AddedAt(ZonedDateTime.now()),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))
                .conversationEventType())
                .isEqualTo(ConversationEventType.PARTICIPANT_ADDED);
    }

    @Test
    public void should_return_event_from() {
        assertThat(new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), new AddedAt(ZonedDateTime.now()),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))
                .eventFrom())
                .isEqualTo(new EventFrom(new ParticipantIdentifier("Mario")));
    }

    @Test
    public void should_return_event_at() {
        final ZonedDateTime at = ZonedDateTime.now();

        assertThat(new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), new AddedAt(at),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))
                .eventAt())
                .isEqualTo(new EventAt(at));
    }

    @Test
    public void should_return_content_empty() {
        assertThat(new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), new AddedAt(ZonedDateTime.now()),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))
                .content())
                .isEqualTo(new Content(""));
    }

    @Test
    public void should_fail_when_return_message() {
        final ZonedDateTime at = ZonedDateTime.now();

        assertThatThrownBy(() -> new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), new AddedAt(ZonedDateTime.now()),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))).toMessage())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void should_return_participants_identifier() {
        assertThat(new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), new AddedAt(ZonedDateTime.now()),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")))
                .participantsIdentifier())
                .containsExactly(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"));
    }
}
