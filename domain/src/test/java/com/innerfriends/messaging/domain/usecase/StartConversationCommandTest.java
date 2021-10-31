package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Content;
import com.innerfriends.messaging.domain.From;
import com.innerfriends.messaging.domain.StartedAt;
import com.innerfriends.messaging.domain.To;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StartConversationCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(StartConversationCommand.class).verify();
    }

    @Test
    public void should_identifier_return_null() {
        assertThat(new StartConversationCommand(mock(From.class), mock(To.class), mock(StartedAt.class), mock(Content.class)).identifier()).isNull();
    }
}
