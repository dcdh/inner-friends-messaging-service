package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ConversationIdentifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ListConversationEventCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ListConversationEventCommand.class).verify();
    }

    @Test
    public void should_identifier_return_conversation_identifier() {
        // Given
        final ConversationIdentifier conversationIdentifier = mock(ConversationIdentifier.class);

        // When && Then
        assertThat(new ListConversationEventCommand(conversationIdentifier).identifier()).isEqualTo(conversationIdentifier);
    }
}
