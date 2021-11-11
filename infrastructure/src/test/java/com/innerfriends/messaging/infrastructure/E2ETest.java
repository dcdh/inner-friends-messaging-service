package com.innerfriends.messaging.infrastructure;

import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.usecase.AddContactIntoContactBookCommand;
import com.innerfriends.messaging.domain.usecase.CreateContactBookCommand;
import com.innerfriends.messaging.infrastructure.bus.producer.KafkaConnectorApi;
import com.innerfriends.messaging.infrastructure.bus.producer.OutboxConnectorStarter;
import com.innerfriends.messaging.infrastructure.usecase.ManagedAddContactIntoContactBookUseCase;
import com.innerfriends.messaging.infrastructure.usecase.ManagedCreateContactBookUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.serialization.JsonObjectDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

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

    @ConfigProperty(name = "kafka.exposed.port.9092")
    Integer kafkaExposedPort9092;

    @Test
    @Order(1)
    public void should_start_debezium_connector() {
        // When
        outboxConnectorStarter.start();

        // Then
        Awaitility.await()
                .atMost(Durations.FIVE_SECONDS)
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                .until(() ->
                        "RUNNING".equals(kafkaConnectorApi.connectorStatus("outbox-connector").connector.state));
    }

    @Test
    @Order(2)
    public void should_create_contact_book() {
        // TODO plug kafka consumer from user domain
        managedCreateContactBookUseCase.execute(new CreateContactBookCommand(new ContactIdentifier("Mario")));
        // TODO tests traces when kafka consumer plugged and traces given from other service
    }

    @Test
    @Order(3)
    public void should_add_contact_into_contact_book() {
        // TODO plug kafka consumer from user domain
        managedAddContactIntoContactBookUseCase.execute(new AddContactIntoContactBookCommand(new Owner("Mario"),
                new ContactIdentifier("Peach")));
        // TODO tests traces when kafka consumer plugged and traces given from other service
    }

    @Test
    @Order(4)
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
    @Order(5)
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
    @Order(6)
    public void should_open_a_new_conversation() throws Exception {
        given()
                .param("from", "Mario")
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
    @Order(7)
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
    @Order(8)
    public void should_list_messages_in_conversation() throws Exception {
        final String conversationIdentifier = getConversationIdentifier().identifier();
        given()
                .when()
                .get("/conversations/{conversationIdentifier}/messages", conversationIdentifier)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(2));
        final TracesUtils.Traces traces = tracesUtils.getTraces(String.format("/conversations/%s/messages", conversationIdentifier));
        assertThat(traces.getOperationNames()).containsExactlyInAnyOrder(
                "conversations/{conversationIdentifier}/messages",
                "PostgresConversationRepository:getConversation");
        assertThat(traces.getHttpStatus()).containsExactlyInAnyOrder(200);
        assertThat(traces.getOperationNamesInError()).isEmpty();
    }

    @Test
    @Order(9)
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
    @Order(10)
    public void should_have_produced_kafka_messages_from_outbox() {
        // TODO use redpanda instead of kafka
        final String bootstrapServers = "localhost:" + kafkaExposedPort9092;
        final List<ConsumerRecord<String, JsonObject>> contactBookEvents = consumeMessages4Topic(bootstrapServers, "ContactBook.events", 2);
        assertThat(contactBookEvents.get(0).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(contactBookEvents.get(0).headers().lastHeader("id").value()).isEqualTo(contactBookEvents.get(0).key().getBytes());
        assertThat(contactBookEvents.get(0).headers().lastHeader("eventType").value()).isEqualTo("ContactBookCreated".getBytes());
        assertThat(contactBookEvents.get(0).headers().lastHeader("aggregateId").value()).isNotNull();
        assertThat(contactBookEvents.get(1).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(contactBookEvents.get(1).headers().lastHeader("id").value()).isEqualTo(contactBookEvents.get(1).key().getBytes());
        assertThat(contactBookEvents.get(1).headers().lastHeader("eventType").value()).isEqualTo("ContactAddedIntoContactBook".getBytes());
        assertThat(contactBookEvents.get(1).headers().lastHeader("aggregateId").value()).isNotNull();
        final List<ConsumerRecord<String, JsonObject>> conversationEvents = consumeMessages4Topic(bootstrapServers, "Conversation.events", 2);
        assertThat(conversationEvents.get(0).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(conversationEvents.get(0).headers().lastHeader("id").value()).isEqualTo(conversationEvents.get(0).key().getBytes());
        assertThat(conversationEvents.get(0).headers().lastHeader("eventType").value()).isEqualTo("NewConversationOpened".getBytes());
        assertThat(conversationEvents.get(0).headers().lastHeader("aggregateId").value()).isNotNull();
        assertThat(conversationEvents.get(1).key()).matches("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
        assertThat(conversationEvents.get(1).headers().lastHeader("id").value()).isEqualTo(conversationEvents.get(1).key().getBytes());
        assertThat(conversationEvents.get(1).headers().lastHeader("eventType").value()).isEqualTo("NewMessagePostedToConversation".getBytes());
        assertThat(conversationEvents.get(1).headers().lastHeader("aggregateId").value()).isNotNull();
    }

    public ConversationIdentifier getConversationIdentifier() throws Exception {
        return runInTransaction(() -> {
            final String conversationIdentifier = entityManager.createQuery("SELECT conversationIdentifier FROM ConversationEntity", String.class)
                    .getResultList().get(0);
            return new ConversationIdentifier(conversationIdentifier);
        });
    }

    private List<ConsumerRecord<String, JsonObject>> consumeMessages4Topic(final String bootstrapServers,
                                                                           final String topic,
                                                                           final int expectedRecordCount) {
        final KafkaConsumer kafkaConsumer = new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
                new StringDeserializer(),
                new JsonObjectDeserializer());
        kafkaConsumer.subscribe(List.of(topic));
        return drain(kafkaConsumer, expectedRecordCount);
    }

    private List<ConsumerRecord<String, JsonObject>> drain(final KafkaConsumer<String, JsonObject> consumer,
                                                           final int expectedRecordCount) {
        final List<ConsumerRecord<String, JsonObject>> allRecords = new ArrayList<>();
        Awaitility.await()
                .atMost(Durations.TEN_SECONDS)
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS).until(() -> {
            consumer.poll(java.time.Duration.ofMillis(50))
                    .iterator()
                    .forEachRemaining(allRecords::add);
            return allRecords.size() >= expectedRecordCount;
        });
        return allRecords;
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
