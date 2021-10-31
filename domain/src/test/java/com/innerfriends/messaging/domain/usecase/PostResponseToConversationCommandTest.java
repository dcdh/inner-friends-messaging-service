package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Content;
import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.From;
import com.innerfriends.messaging.domain.PostedAt;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PostResponseToConversationCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(PostResponseToConversationCommand.class).verify();
    }

    @Test
    public void should_identifier_return_conversation_identifier() {
        // Given
        final ConversationIdentifier conversationIdentifier = mock(ConversationIdentifier.class);

        // When && Then
        assertThat(new PostResponseToConversationCommand(mock(From.class), conversationIdentifier, mock(Content.class), mock(PostedAt.class))
                .identifier()).isEqualTo(conversationIdentifier);
    }
}
