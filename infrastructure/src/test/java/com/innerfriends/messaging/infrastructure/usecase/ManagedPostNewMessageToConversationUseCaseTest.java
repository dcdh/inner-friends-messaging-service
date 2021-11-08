package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.domain.usecase.PostNewMessageToConversationCommand;
import com.innerfriends.messaging.domain.usecase.PostNewMessageToConversationUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedPostNewMessageToConversationUseCaseTest extends ManagedUseCaseTest {

    @Inject
    ManagedPostNewMessageToConversationUseCase managedPostNewMessageToConversationUseCase;

    @InjectMock
    PostNewMessageToConversationUseCase postNewMessageToConversationUseCase;

    @InjectMock
    InstantProvider instantProvider;

    @Test
    public void should_post_new_message_to_conversation() {
        // Given
        final PostNewMessageToConversationCommand postNewMessageToConversationCommand = new PostNewMessageToConversationCommand(
                new From("Mario"), new ConversationIdentifier("Mario-azerty"), new Content("Hi everyone !"));
        final Conversation conversation = new Conversation(
                new ConversationIdentifier("Mario-azerty"),
                List.of(new Message(new From("Mario"), new PostedAt(ZonedDateTime.now()), new Content("Hi Peach how are you ?")),
                        new Message(new From("Peach"), new PostedAt(ZonedDateTime.now()), new Content("Fine thanks you !"))),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")),
                1l);
        doReturn(conversation).when(postNewMessageToConversationUseCase).execute(postNewMessageToConversationCommand);
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When && Then
        assertThat(managedPostNewMessageToConversationUseCase.execute(postNewMessageToConversationCommand))
                .isEqualTo(conversation);
        assertThat(((Number) entityManager.createNativeQuery(COUNT_OUTBOX_EVENT_FOR_AGGREGATE_ID_SQL)
                .setParameter(1, "Mario-azerty")
                .getSingleResult()).longValue()).isEqualTo(1l);
        assertThat(entityManager.createNativeQuery(GET_TYPE_BY_AGGREGATE_ID)
                .setParameter(1, "Mario-azerty")
                .getSingleResult())
                .isEqualTo("NewMessagePostedToConversation");
    }
}
