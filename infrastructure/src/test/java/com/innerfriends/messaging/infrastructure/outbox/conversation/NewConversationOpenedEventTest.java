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
public class NewConversationOpenedEventTest {

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
                new Message(new From("Mario"), buildPostedAt(2), new Content("Hi Peach how are you ?")),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")));
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When
        final NewConversationOpenedEvent newConversationOpenedEvent = NewConversationOpenedEvent.of(conversation, objectMapper, instantProvider);

        // Then
        assertThat(newConversationOpenedEvent.getAggregateId()).isEqualTo("Mario-azerty");
        assertThat(newConversationOpenedEvent.getAggregateType()).isEqualTo("Conversation");
        assertThat(newConversationOpenedEvent.getTimestamp()).isEqualTo(Instant.ofEpochSecond(1));
        assertThat(newConversationOpenedEvent.getType()).isEqualTo("NewConversationOpened");
        assertThat(newConversationOpenedEvent.getPayload().toString()).isEqualTo("{\"conversationIdentifier\":\"Mario-azerty\",\"version\":0,\"firstMessage\":{\"from\":\"Mario\",\"content\":\"Hi Peach how are you ?\",\"postedAt\":\"2021-10-02T00:00:00+02:00[Europe/Paris]\"},\"participantsIdentifier\":[\"Mario\",\"Peach\"]}");
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
