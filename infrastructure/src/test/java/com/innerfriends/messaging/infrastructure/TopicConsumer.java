package com.innerfriends.messaging.infrastructure;

import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.serialization.JsonObjectDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TopicConsumer {

    private final Logger logger = LoggerFactory.getLogger(TopicConsumer.class);

    private final KafkaConsumer<String, JsonObject> kafkaConsumer;

    public TopicConsumer(@ConfigProperty(name = "kafka.exposed.port.9092") final Integer kafkaExposedPort9092) {
        final String bootstrapServers = "localhost:" + kafkaExposedPort9092;
        kafkaConsumer = new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "test-group-consumer",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
                new StringDeserializer(),
                new JsonObjectDeserializer());
        if (!kafkaConsumer.listTopics().containsKey("ContactBook.events")) {
            throw new IllegalStateException("topic ContactBook.events not present and can't be subscribe");
        }
        if (!kafkaConsumer.listTopics().containsKey("Conversation.events")) {
            throw new IllegalStateException("topic Conversation.events not present and can't be subscribe");
        }
        kafkaConsumer.assign(List.of(new TopicPartition("ContactBook.events", 0),
                new TopicPartition("Conversation.events", 0)));
    }

    public List<ConsumerRecord<String, JsonObject>> drain(final int expectedRecordCount) {
        final List<ConsumerRecord<String, JsonObject>> allRecords = new ArrayList<>();
        Awaitility.await()
                .atMost(Durations.TEN_SECONDS)
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS).until(() -> {
            kafkaConsumer.poll(java.time.Duration.ofMillis(50))
                    .iterator()
                    .forEachRemaining(record -> {
                        allRecords.add(record);
                        logger.info(record.toString());
                    });
            return allRecords.size() >= expectedRecordCount;
        });
        return allRecords;
    }

}
