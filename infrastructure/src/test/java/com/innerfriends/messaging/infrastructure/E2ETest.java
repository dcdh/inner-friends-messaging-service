package com.innerfriends.messaging.infrastructure;

import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.ParticipantIdentifier;
import com.innerfriends.messaging.domain.usecase.AddContactIntoContactBookCommand;
import com.innerfriends.messaging.domain.usecase.AddParticipantIntoConversationCommand;
import com.innerfriends.messaging.domain.usecase.CreateContactBookCommand;
import com.innerfriends.messaging.infrastructure.bus.producer.KafkaConnectorApi;
import com.innerfriends.messaging.infrastructure.bus.producer.OutboxConnectorStarter;
import com.innerfriends.messaging.infrastructure.usecase.ManagedAddContactIntoContactBookUseCase;
import com.innerfriends.messaging.infrastructure.usecase.ManagedAddParticipantIntoConversationUseCase;
import com.innerfriends.messaging.infrastructure.usecase.ManagedCreateContactBookUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class E2ETest {

    @Inject
    ManagedCreateContactBookUseCase managedCreateContactBookUseCase;

    @Inject
    ManagedAddContactIntoContactBookUseCase managedAddContactIntoContactBookUseCase;

    @Inject
    ManagedAddParticipantIntoConversationUseCase managedAddParticipantIntoConversationUseCase;

    @Inject
    OutboxConnectorStarter outboxConnectorStarter;

    @Inject
    @RestClient
    KafkaConnectorApi kafkaConnectorApi;

    @Inject
    UserTransaction userTransaction;

    @Inject
    EntityManager entityManager;

    @Inject
    TracesUtils tracesUtils;

    @Inject
    TopicConsumer topicConsumer;

    @BeforeEach
    public void setup() {
        outboxConnectorStarter.start();
        Awaitility.await()
                .atMost(Durations.FIVE_SECONDS)
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                .until(() ->
                        "RUNNING".equals(kafkaConnectorApi.connectorStatus("outbox-connector").connector.state));
    }

    @Test
    @Order(1)
    public void should_create_contact_book() {
        // TODO plug kafka consumer from friend domain
        managedCreateContactBookUseCase.execute(new CreateContactBookCommand(new ContactIdentifier("Mario")));
        // TODO tests traces when kafka consumer plugged and traces given from other service
    }

    @Test
    @Order(2)
    public void should_add_contact_into_contact_book() {
        // TODO plug kafka consumer from friend domain
        managedAddContactIntoContactBookUseCase.execute(new AddContactIntoContactBookCommand(new Owner("Mario"),
                new ContactIdentifier("Peach")));
        // TODO tests traces when kafka consumer plugged and traces given from other service
    }

    @Test
    @Order(3)
    public void should_list_all_contacts() throws Exception {
        given()
                .when()
                .get("/contacts/Mario")
                .then()
                .log().all()
                .statusCode(200)
                .body("owner", equalTo("Mario"))
                .body("contacts.size()", equalTo(1));
        final TracesUtils.Traces traces = tracesUtils.getTraces("/contacts/Mario");
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder(
                "PostgresContactBookRepository:getByOwner",
                "HazelcastContactBookCacheRepository:getByOwner",
                "HazelcastContactBookCacheRepository:store",
                "contacts/{owner}");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(4)
    public void should_list_recent_contacts() throws Exception {
        given()
                .param("nbOfContactToReturn", 2)
                .when()
                .post("/contacts/Mario/recent")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(1));
        final TracesUtils.Traces traces = tracesUtils.getTraces("/contacts/Mario/recent");
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder(
                "contacts/{owner}/recent",
                "PostgresContactBookRepository:getByOwner");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(5)
    public void should_open_a_new_conversation() throws Exception {
        given()
                .param("openedBy", "Mario")
                .param("to", "Peach")
                .param("content", "Hello Peach how are you ?")
                .when()
                .post("/conversations/openANewOne")
                .then()
                .log().all()
                .statusCode(200);
        final TracesUtils.Traces traces = tracesUtils.getTraces("/conversations/openANewOne");
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder(
                "conversations/openANewOne",
                "PostgresConversationRepository:createConversation",
                "PostgresContactBookRepository:getByOwner");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(6)
    public void should_post_a_new_message_to_conversation() throws Exception {
        final String conversationIdentifier = getConversationIdentifier().identifier();
        given()
                .param("conversationIdentifier", conversationIdentifier)
                .param("from", "Peach")
                .param("content", "I am fine thanks")
                .when()
                .post("/conversations/postNewMessage")
                .then()
                .log().all()
                .statusCode(204);
        final TracesUtils.Traces traces = tracesUtils.getTraces("/conversations/postNewMessage");
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder(
                "conversations/postNewMessage",
                "PostgresConversationRepository:getConversation",
                "PostgresConversationRepository:saveConversation");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(204);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(7)
    public void should_list_events_in_conversation() throws Exception {
        final String conversationIdentifier = getConversationIdentifier().identifier();
        given()
                .when()
                .get("/conversations/{conversationIdentifier}/events", conversationIdentifier)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(2));
        final TracesUtils.Traces traces = tracesUtils.getTraces(String.format("/conversations/%s/events", conversationIdentifier));
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder(
                "conversations/{conversationIdentifier}/events",
                "PostgresConversationRepository:getConversation");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(8)
    public void should_list_participant_conversations() throws Exception {
        given()
                .param("participantIdentifier", "Mario")
                .when()
                .post("/conversations/listConversations")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(1));
        final TracesUtils.Traces traces = tracesUtils.getTraces("/conversations/listConversations");
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder(
                "conversations/listConversations",
                "PostgresConversationRepository:getConversationsForParticipant");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(9)
    public void should_add_participant_into_conversation() throws Exception {
        // TODO plug kafka consumer from friend domain
        final String conversationIdentifier = getConversationIdentifier().identifier();
        managedAddParticipantIntoConversationUseCase.execute(
                new AddParticipantIntoConversationCommand(
                        new ConversationIdentifier(conversationIdentifier), new ParticipantIdentifier("Luigi")));
        // TODO tests traces when kafka consumer plugged and traces given from other service
    }

    @Test
    @Order(10)
    public void should_have_produced_kafka_messages_from_outbox() throws Exception {
        final String conversationIdentifier = getConversationIdentifier().identifier();
        final List<ConsumerRecord<String, JsonObject>> consumerRecords = topicConsumer.drain(5);
        final List<ConsumerRecord<String, JsonObject>> contactBookEvents = consumerRecords.stream()
                .filter(consumerRecord -> "ContactBook.events".equals(consumerRecord.topic())).collect(Collectors.toList());
        assertThat(contactBookEvents.get(0).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(contactBookEvents.get(0).headers().lastHeader("id").value()).isEqualTo(contactBookEvents.get(0).key().getBytes());
        assertThat(contactBookEvents.get(0).headers().lastHeader("eventType").value()).isEqualTo("ContactBookCreated".getBytes());
        assertThat(contactBookEvents.get(0).headers().lastHeader("aggregateId").value()).isEqualTo("Mario".getBytes());
        assertThat(contactBookEvents.get(1).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(contactBookEvents.get(1).headers().lastHeader("id").value()).isEqualTo(contactBookEvents.get(1).key().getBytes());
        assertThat(contactBookEvents.get(1).headers().lastHeader("eventType").value()).isEqualTo("ContactAddedIntoContactBook".getBytes());
        assertThat(contactBookEvents.get(1).headers().lastHeader("aggregateId").value()).isEqualTo("Mario".getBytes());
        final List<ConsumerRecord<String, JsonObject>> conversationEvents = consumerRecords.stream()
                .filter(consumerRecord -> "Conversation.events".equals(consumerRecord.topic())).collect(Collectors.toList());
        assertThat(conversationEvents.get(0).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(conversationEvents.get(0).headers().lastHeader("id").value()).isEqualTo(conversationEvents.get(0).key().getBytes());
        assertThat(conversationEvents.get(0).headers().lastHeader("eventType").value()).isEqualTo("NewConversationOpened".getBytes());
        assertThat(conversationEvents.get(0).headers().lastHeader("aggregateId").value()).isEqualTo(conversationIdentifier.getBytes());
        assertThat(conversationEvents.get(1).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(conversationEvents.get(1).headers().lastHeader("id").value()).isEqualTo(conversationEvents.get(1).key().getBytes());
        assertThat(conversationEvents.get(1).headers().lastHeader("eventType").value()).isEqualTo("NewMessagePostedToConversation".getBytes());
        assertThat(conversationEvents.get(1).headers().lastHeader("aggregateId").value()).isEqualTo(conversationIdentifier.getBytes());
        assertThat(conversationEvents.get(2).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(conversationEvents.get(2).headers().lastHeader("id").value()).isEqualTo(conversationEvents.get(2).key().getBytes());
        assertThat(conversationEvents.get(2).headers().lastHeader("eventType").value()).isEqualTo("ParticipantAddedIntoConversation".getBytes());
        assertThat(conversationEvents.get(2).headers().lastHeader("aggregateId").value()).isEqualTo(conversationIdentifier.getBytes());
    }

    public ConversationIdentifier getConversationIdentifier() throws Exception {
        return runInTransaction(() -> {
            final String conversationIdentifier = entityManager.createQuery("SELECT conversationIdentifier FROM ConversationEntity", String.class)
                    .getResultList().get(0);
            return new ConversationIdentifier(conversationIdentifier);
        });
    }

    private <V> V runInTransaction(final Callable<V> callable) throws Exception {
        userTransaction.begin();
        try {
            return callable.call();
        } catch (final Exception exception) {
            throw exception;
        } finally {
            userTransaction.commit();
        }
    }

}
