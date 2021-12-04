package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.Message;
import com.innerfriends.messaging.domain.usecase.ListConversationEventCommand;
import com.innerfriends.messaging.domain.usecase.ListConversationEventUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedListConversationEventUseCaseTest {

    @Inject
    ManagedListConversationEventUseCase managedListConversationEventUseCase;

    @InjectMock
    ListConversationEventUseCase listConversationEventUseCase;

    @Test
    public void should_list_messages_in_conversation() {
        // Given
        final ListConversationEventCommand listConversationEventCommand
                = new ListConversationEventCommand(new ConversationIdentifier("Mario-azerty"));
        final List<Message> messages = List.of(mock(Message.class));
        doReturn(messages).when(listConversationEventUseCase).execute(listConversationEventCommand);

        // When && Then
        assertThat(managedListConversationEventUseCase.execute(listConversationEventCommand))
                .isEqualTo(messages);
    }
}
