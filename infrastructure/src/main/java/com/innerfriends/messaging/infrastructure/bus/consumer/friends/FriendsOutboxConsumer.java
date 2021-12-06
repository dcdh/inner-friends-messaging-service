package com.innerfriends.messaging.infrastructure.bus.consumer.friends;

import com.innerfriends.messaging.infrastructure.bus.consumer.KafkaEventConsumer;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class FriendsOutboxConsumer extends KafkaEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(FriendsOutboxConsumer.class);

    private final FriendsOutboxHandler friendsOutboxHandler;
    private final String groupId;

    public FriendsOutboxConsumer(final FriendsOutboxHandler friendsOutboxHandler,
                                 @ConfigProperty(name = "mp.messaging.incoming.friends.group.id") final String groupId) {
        this.friendsOutboxHandler = Objects.requireNonNull(friendsOutboxHandler);
        this.groupId = Objects.requireNonNull(groupId);
    }

    @Incoming("friends")
    public CompletionStage<Void> onMessage(final IncomingKafkaRecord<UUID, JsonObject> message) {
        return CompletableFuture.runAsync(() -> {
            try {
                final String eventType = getHeaderAsString(message, "eventType");
                final String aggregateId = getHeaderAsString(message, "aggregateId");
                final UUID eventId = message.getKey();

                friendsOutboxHandler.onEvent(groupId,
                        eventId,
                        eventType,
                        aggregateId,
                        message.getPayload(),
                        message.getTimestamp());
            } catch (Exception e) {
                LOG.error("Error while consuming friends event", e.getCause());
                throw e;
            }
        }).thenRun(() -> message.ack());
    }

}
