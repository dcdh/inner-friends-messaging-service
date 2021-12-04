package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.domain.usecase.AddParticipantIntoConversationCommand;
import com.innerfriends.messaging.domain.usecase.AddParticipantIntoConversationUseCase;
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
public class ManagedAddParticipantIntoConversationUseCaseTest extends ManagedUseCaseTest {

    @Inject
    ManagedAddParticipantIntoConversationUseCase managedAddParticipantIntoConversationUseCase;

    @InjectMock
    AddParticipantIntoConversationUseCase addParticipantIntoConversationUseCase;

    @InjectMock
    InstantProvider instantProvider;

    @Test
    public void should_add_participant_into_conversation() {
        // Given
        final AddParticipantIntoConversationCommand addParticipantIntoConversationCommand = new AddParticipantIntoConversationCommand(
                new ConversationIdentifier("Mario-azerty"), new ParticipantIdentifier("Luigi"));
        final Conversation conversation = new Conversation(
                new ConversationIdentifier("Mario-azerty"),
                List.of(
                        new StartedConversationEvent(new Message(new From("Mario"), new PostedAt(ZonedDateTime.now()), new Content("Hi Peach how are you ?")),
                                List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))),
                        new MessagePostedConversationEvent(new Message(new From("Peach"), new PostedAt(ZonedDateTime.now()), new Content("Fine thanks you !"))),
                        new ParticipantAddedConversationEvent(new ParticipantIdentifier("Luigi"), new AddedAt(ZonedDateTime.now()))),
                1l);
        doReturn(conversation).when(addParticipantIntoConversationUseCase).execute(addParticipantIntoConversationCommand);
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When && Then
        assertThat(managedAddParticipantIntoConversationUseCase.execute(addParticipantIntoConversationCommand))
                .isEqualTo(conversation);
        assertThat(((Number) entityManager.createNativeQuery(COUNT_OUTBOX_EVENT_FOR_AGGREGATE_ID_SQL)
                .setParameter(1, "Mario-azerty")
                .getSingleResult()).longValue()).isEqualTo(1l);
        assertThat(entityManager.createNativeQuery(GET_TYPE_BY_AGGREGATE_ID)
                .setParameter(1, "Mario-azerty")
                .getSingleResult())
                .isEqualTo("ParticipantAddedIntoConversation");
    }

}