package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Content;
import com.innerfriends.messaging.domain.OpenedBy;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OpenNewConversationCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(OpenNewConversationCommand.class).verify();
    }

    @Test
    public void should_identifier_return_null() {
        assertThat(new OpenNewConversationCommand(mock(OpenedBy.class), Collections.emptyList(), mock(Content.class)).identifier()).isNull();
    }
}
