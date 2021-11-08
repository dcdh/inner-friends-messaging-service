package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;

import javax.inject.Inject;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class PostgresConversationRepositoryTest extends RepositoryTesting {

    private static final String CREATE_CONVERSATION_SQL = "INSERT INTO public.T_CONVERSATION VALUES ('Mario-azerty', '[]', '[]', 0)";
    private static final String COUNT_CONVERSATION_SQL = "SELECT COUNT(*) FROM public.T_CONVERSATION";

    @Inject
    PostgresConversationRepository postgresConversationRepository;

    @Test
    public void should_return_conversation() throws Exception {
        // Given
        runInTransaction(() ->
                entityManager.createNativeQuery(CREATE_CONVERSATION_SQL).executeUpdate());

        // When
        final Conversation conversation = runInTransaction(() -> postgresConversationRepository.getConversation(new ConversationIdentifier("Mario-azerty")));

        // Then
        assertThat(conversation).isEqualTo(
                new Conversation(
                        new ConversationIdentifier("Mario-azerty"), Collections.emptyList(), Collections.emptyList(),0l));
    }

    @Test
    public void should_get_conversion_fail_when_conversation_does_not_exists() {
        assertThatThrownBy(() -> runInTransaction(() -> postgresConversationRepository.getConversation(new ConversationIdentifier("Mario-azerty"))))
                .isInstanceOf(UnknownConversationException.class);
    }

    @Test
    public void should_create_conversation() throws Exception {
        // Given
        final Conversation conversationToCreate = new Conversation(new ConversationIdentifier("Mario-azerty"),
                List.of(
                        new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?"))),
                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario")),
                0l);

        // When
        runInTransaction(() -> {
            postgresConversationRepository.createConversation(conversationToCreate);
            return null;
        });

        // Then
        assertThat(((Number) entityManager.createNativeQuery(COUNT_CONVERSATION_SQL).getSingleResult()).longValue()).isEqualTo(1l);
    }

    @Test
    public void should_create_conversation_fail_when_already_exists() throws Exception {
        // Given
        final Conversation conversationToCreate = new Conversation(new ConversationIdentifier("Mario-azerty"),
                Collections.emptyList(),
                Collections.emptyList(),
                0l);
        runInTransaction(() ->
                entityManager.createNativeQuery(CREATE_CONVERSATION_SQL).executeUpdate());

        // When
        assertThatThrownBy(() -> runInTransaction(() -> {
            postgresConversationRepository.createConversation(conversationToCreate);
            return null;
        }))
                .getRootCause()
                .isInstanceOf(PSQLException.class)
                .hasMessage("ERROR: duplicate key value violates unique constraint \"t_conversation_pkey\"\n" +
                        "  Détail : Key (conversationidentifier)=(Mario-azerty) already exists.");
    }

    @Test
    public void should_save_conversation() throws Exception {
        // Given
        runInTransaction(() ->
                entityManager.createNativeQuery(CREATE_CONVERSATION_SQL).executeUpdate());
        final Conversation conversationToSave = new Conversation(
                new ConversationIdentifier("Mario-azerty"),
                List.of(
                        new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?"))),
                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario")),
                1l
        );

        // When
        runInTransaction(() -> {
            postgresConversationRepository.saveConversation(conversationToSave);
            return null;
        });

        // Then
        final Conversation conversation = runInTransaction(() -> postgresConversationRepository.getConversation(new ConversationIdentifier("Mario-azerty")));
        assertThat(conversation).isEqualTo(
                new Conversation(
                        new ConversationIdentifier("Mario-azerty"),
                        List.of(
                                new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?"))),
                        List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario")),
                        1l));
    }

    @Test
    public void should_fail_when_next_version_is_not_incremented_by_one() throws Exception {
        // Given
        runInTransaction(() ->
                entityManager.createNativeQuery(CREATE_CONVERSATION_SQL).executeUpdate());
        final Conversation conversationToSave = new Conversation(
                new ConversationIdentifier("Mario-azerty"),
                List.of(
                        new Message(new From("Peach"), buildPostedAt(2), new Content("Hi Mario How are you ?"))),
                List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Mario")),
                2l
        );

        // When && Then
        assertThatThrownBy(() -> runInTransaction(() -> {
            postgresConversationRepository.saveConversation(conversationToSave);
            return null;
        }))
                .getRootCause()
                .isInstanceOf(PSQLException.class)
                .hasMessage("ERROR: Conversation version unexpected on update for owner Mario-azerty - current version 2 - expected version 1\n" +
                        "  Où : PL/pgSQL function conversation_check_version_on_update() line 6 at RAISE");
    }

    @Test
    public void should_get_conversations_for_participants() throws Exception {
        // Given
        runInTransaction(() -> {
            postgresConversationRepository.createConversation(new Conversation(
                    new ConversationIdentifier("Mario-azerty"),
                    new Message(new From("Mario"), buildPostedAt(2), new Content("Hi Peach How are you ?")),
                    List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))
            ));
            return null;
        });
        runInTransaction(() -> {
            postgresConversationRepository.createConversation(new Conversation(
                    new ConversationIdentifier("Luigi-azerty"),
                    new Message(new From("Luigi"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                    List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Luigi"))
            ));
            return null;
        });
        runInTransaction(() -> {
            postgresConversationRepository.createConversation(new Conversation(
                    new ConversationIdentifier("Peach-azerty"),
                    new Message(new From("Luigi"), buildPostedAt(2), new Content("Hi Luigi How are you ?")),
                    List.of(new ParticipantIdentifier("Peach"), new ParticipantIdentifier("Luigi"))
            ));
            return null;
        });

        // When
        final List<Conversation> marioConversations = runInTransaction(() -> postgresConversationRepository
                .getConversationsForParticipant(new ParticipantIdentifier("Mario")));
        assertThat(marioConversations).containsExactly(
                new Conversation(
                        new ConversationIdentifier("Mario-azerty"),
                        new Message(new From("Mario"), buildPostedAt(2), new Content("Hi Peach How are you ?")),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Peach"))
                ),
                new Conversation(
                        new ConversationIdentifier("Luigi-azerty"),
                        new Message(new From("Luigi"), buildPostedAt(2), new Content("Hi Mario How are you ?")),
                        List.of(new ParticipantIdentifier("Mario"), new ParticipantIdentifier("Luigi"))
                )
        );
    }

    private PostedAt buildPostedAt(final Integer day) {
        return new PostedAt(
                ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
