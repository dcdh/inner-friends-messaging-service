package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.domain.usecase.OpenANewConversationCommand;
import com.innerfriends.messaging.domain.usecase.OpenANewConversationUseCase;
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
public class ManagedOpenANewConversationUseCaseTest extends ManagedUseCaseTest {

    @Inject
    ManagedOpenANewConversationUseCase managedOpenANewConversationUseCase;

    @InjectMock
    OpenANewConversationUseCase openANewConversationUseCase;

    @InjectMock
    InstantProvider instantProvider;

    @Test
    public void should_open_a_new_conversation() {
        // Given
        final OpenANewConversationCommand openANewConversationCommand = new OpenANewConversationCommand(
                new OpenedBy(new ParticipantIdentifier("Mario")),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")),
                new Content("Hi Peach how are you ?"));
        final Conversation conversation = new Conversation(
                new ConversationIdentifier("Mario-azerty"),
                new Message(new From("Mario"), new PostedAt(ZonedDateTime.now()), new Content("Hi Peach how are you ?")),
                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach")));
        doReturn(conversation).when(openANewConversationUseCase).execute(openANewConversationCommand);
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When && Then
        assertThat(managedOpenANewConversationUseCase.execute(openANewConversationCommand))
                .isEqualTo(conversation);
        assertThat(((Number) entityManager.createNativeQuery(COUNT_OUTBOX_EVENT_FOR_AGGREGATE_ID_SQL)
                .setParameter(1, "Mario-azerty")
                .getSingleResult()).longValue()).isEqualTo(1l);
        assertThat(entityManager.createNativeQuery(GET_TYPE_BY_AGGREGATE_ID)
                .setParameter(1, "Mario-azerty")
                .getSingleResult())
                .isEqualTo("NewConversationOpened");
    }
}
