package com.innerfriends.messaging.infrastructure.outbox.conversation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ParticipantAddedIntoConversationEventTest {

    private InstantProvider instantProvider;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        this.instantProvider = mock(InstantProvider.class);
    }

    @Test
    public void should_return_expected_event() {
        // Given
        final ConversationIdentifier conversationIdentifier = new ConversationIdentifier("Mario-azerty");

        final Conversation conversation = new Conversation(conversationIdentifier,
                List.of(new StartedConversationEvent(new Message(new From("Mario"), new PostedAt(ZonedDateTime.now()), new Content("Hi Peach how are you ?")), List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))),
                        new ParticipantAddedConversationEvent(new ParticipantIdentifier("Luigi"), buildAddedAt(2))),
                2l);
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When
        final ParticipantAddedIntoConversationEvent participantAddedIntoConversationEvent = ParticipantAddedIntoConversationEvent
                .of(conversation, objectMapper, instantProvider);

        // Then
        assertThat(participantAddedIntoConversationEvent.getAggregateId()).isEqualTo("Mario-azerty");
        assertThat(participantAddedIntoConversationEvent.getAggregateType()).isEqualTo("Conversation");
        assertThat(participantAddedIntoConversationEvent.getTimestamp()).isEqualTo(Instant.ofEpochSecond(1));
        assertThat(participantAddedIntoConversationEvent.getType()).isEqualTo("ParticipantAddedIntoConversation");
        assertThat(participantAddedIntoConversationEvent.getPayload().toString()).isEqualTo("{\"conversationIdentifier\":\"Mario-azerty\",\"participantAddedIntoConversation\":\"Luigi\",\"addedAt\":\"2021-10-02T00:00:00+02:00[Europe/Paris]\",\"version\":2}");
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}