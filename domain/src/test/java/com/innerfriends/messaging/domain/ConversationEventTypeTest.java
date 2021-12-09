package com.innerfriends.messaging.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConversationEventTypeTest {

    @Test
    public void should_return_message_posted_conversation_event() {
        final ZonedDateTime at = ZonedDateTime.now();

        assertThat(ConversationEventType.MESSAGE_POSTED.toConversationEvent(new EventFrom(new ParticipantIdentifier("Mario")), new EventAt(at), new Content("Hello !"),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))))
                .isEqualTo(new MessagePostedConversationEvent(new Message(new From("Mario"), new PostedAt(at), new Content("Hello !")),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))));
    }

    @Test
    public void should_return_participant_added_conversation_event() {
        final ZonedDateTime at = ZonedDateTime.now();

        assertThat(ConversationEventType.PARTICIPANT_ADDED.toConversationEvent(
                new EventFrom(new ParticipantIdentifier("Mario")),
                new EventAt(at),
                new Content("Hello !"),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))))
                .isEqualTo(new ParticipantAddedConversationEvent(new ParticipantIdentifier("Mario"), new AddedAt(at),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))));
    }

}
