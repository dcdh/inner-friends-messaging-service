package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ConversationTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Conversation.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void should_initialize_version_to_zero() {
        // Given
        final ConversationIdentifier conversationIdentifier = mock(ConversationIdentifier.class);
        final Conversation conversation = new Conversation(
                conversationIdentifier,
                new Message(new From("Mario"), buildPostedAt(1), new Content("Hello Luigi")),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Luigi"))
        );

        // When && Then
        assertThat(conversation)
                .isEqualTo(new Conversation(
                        conversationIdentifier,
                        List.of(
                                new StartedConversationEvent(
                                        new Message(new From("Mario"), buildPostedAt(1), new Content("Hello Luigi")),
                                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Luigi")))),
                        0l
        ));
        assertThat(conversation.version()).isEqualTo(0l);
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }
}
