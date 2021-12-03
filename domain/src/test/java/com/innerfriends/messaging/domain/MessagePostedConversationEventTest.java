package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class MessagePostedConversationEventTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(MessagePostedConversationEvent.class).verify();
    }

    @Test
    public void should_return_message_posted_type() {
        assertThat(new MessagePostedConversationEvent(new Message(new From("Mario"), new PostedAt(ZonedDateTime.now()), new Content("Hello !")))
                .conversationEventType())
                .isEqualTo(ConversationEventType.MESSAGE_POSTED);
    }

    @Test
    public void should_return_event_from() {
        assertThat(new MessagePostedConversationEvent(new Message(new From("Mario"), new PostedAt(ZonedDateTime.now()), new Content("Hello !")))
                .eventFrom())
                .isEqualTo(new EventFrom(new ParticipantIdentifier("Mario")));
    }

    @Test
    public void should_return_event_at() {
        final ZonedDateTime at = ZonedDateTime.now();

        assertThat(new MessagePostedConversationEvent(new Message(new From("Mario"), new PostedAt(at), new Content("Hello !")))
                .eventAt())
                .isEqualTo(new EventAt(at));
    }

    @Test
    public void should_return_content() {
        assertThat(new MessagePostedConversationEvent(new Message(new From("Mario"), new PostedAt(ZonedDateTime.now()), new Content("Hello !")))
                .content())
                .isEqualTo(new Content("Hello !"));
    }

    @Test
    public void should_return_message() {
        final ZonedDateTime at = ZonedDateTime.now();

        assertThat(new MessagePostedConversationEvent(new Message(new From("Mario"), new PostedAt(at), new Content("Hello !")))
                .toMessage())
                .isEqualTo(new Message(new From("Mario"), new PostedAt(at), new Content("Hello !")));
    }

}
