package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ParticipantIdentifier;
import com.innerfriends.messaging.domain.usecase.ListConversationsCommand;
import com.innerfriends.messaging.domain.usecase.ListConversationsUseCase;
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
public class ManagedListConversationsUseCaseTest {

    @Inject
    ManagedListConversationsUseCase managedListConversationsUseCase;

    @InjectMock
    ListConversationsUseCase listConversationsUseCase;

    @Test
    public void should_list_conversations() {
        // Given
        final ListConversationsCommand listConversationsCommand
                = new ListConversationsCommand(new ParticipantIdentifier("Mario"));
        final List<Conversation> conversations = List.of(mock(Conversation.class));
        doReturn(conversations).when(listConversationsUseCase).execute(listConversationsCommand);

        // When && Then
        assertThat(managedListConversationsUseCase.execute(listConversationsCommand))
                .isEqualTo(conversations);
    }
}
