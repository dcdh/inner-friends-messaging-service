package com.innerfriends.messaging.infrastructure.bus.consumer.friends;

import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.usecase.AddContactIntoContactBookCommand;
import com.innerfriends.messaging.domain.usecase.CreateContactBookCommand;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.opentelemetry.NewSpan;
import com.innerfriends.messaging.infrastructure.postgres.MessageLogRepository;
import com.innerfriends.messaging.infrastructure.usecase.ManagedAddContactIntoContactBookUseCase;
import com.innerfriends.messaging.infrastructure.usecase.ManagedCreateContactBookUseCase;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class FriendsOutboxHandler {

    private final Logger LOG = LoggerFactory.getLogger(FriendsOutboxHandler.class);

    private final ManagedCreateContactBookUseCase managedCreateContactBookUseCase;
    private final ManagedAddContactIntoContactBookUseCase managedAddContactIntoContactBookUseCase;
    private final MessageLogRepository messageLogRepository;
    private final InstantProvider instantProvider;

    public FriendsOutboxHandler(final ManagedCreateContactBookUseCase managedCreateContactBookUseCase,
                                final ManagedAddContactIntoContactBookUseCase managedAddContactIntoContactBookUseCase,
                                final MessageLogRepository messageLogRepository,
                                final InstantProvider instantProvider) {
        this.managedCreateContactBookUseCase = Objects.requireNonNull(managedCreateContactBookUseCase);
        this.managedAddContactIntoContactBookUseCase = Objects.requireNonNull(managedAddContactIntoContactBookUseCase);
        this.messageLogRepository = Objects.requireNonNull(messageLogRepository);
        this.instantProvider = Objects.requireNonNull(instantProvider);
    }

    @NewSpan
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public final void onEvent(final String groupId,
                              final UUID eventId,
                              final String eventType,
                              final String aggregateId,
                              final JsonObject eventPayload,
                              final Instant messageInstant) {
        LOG.info("Received event -- groupId: {}, aggregateId: {}, event id: '{}', event type: '{}', message timestamp: '{}'", groupId, aggregateId, eventId, eventType, messageInstant);

        if (messageLogRepository.alreadyProcessed(groupId, eventId)) {
            LOG.info("Event with UUID {} was already processed, ignoring it", eventId);
            return;
        }
        if ("NewFriendRegisteredIntoThePlatform".equals(eventType)) {
            /**
             * {
             *   "friendId": "Mario",
             *   "version": 0,
             *   "inFriendshipsWith": [
             *     "DamDamDeo"
             *   ]
             * }
             */
            final List<ContactIdentifier> contactIdentifiers = eventPayload.getJsonArray("inFriendshipsWith")
                    .stream()
                    .map(String.class::cast)
                    .map(ContactIdentifier::new)
                    .collect(Collectors.toList());
            managedCreateContactBookUseCase.execute(new CreateContactBookCommand(
                    new Owner(eventPayload.getString("friendId")), contactIdentifiers));
        } else if ("FromFriendEstablishedAFriendshipWithToFriend".equals(eventType)) {
            /**
             * {
             *   "friendId": "Mario",
             *   "establishedFriendshipWith": "Luigi",
             *   "version": 1
             * }
             */
            managedAddContactIntoContactBookUseCase.execute(new AddContactIntoContactBookCommand(
                    new Owner(eventPayload.getString("friendId")),
                    new ContactIdentifier(eventPayload.getString("establishedFriendshipWith"))));
        } else if ("ToFriendEstablishedAFriendshipWithFromFriend".equals(eventType)) {
            /**
             * {
             *   "friendId": "Mario",
             *   "establishedFriendshipWith": "DamDamDeo",
             *   "version": 1
             * }
             */
            managedAddContactIntoContactBookUseCase.execute(new AddContactIntoContactBookCommand(
                    new Owner(eventPayload.getString("friendId")),
                    new ContactIdentifier(eventPayload.getString("establishedFriendshipWith"))));
        } else {
            LOG.info("Unknown event type '{}' for event id '{}', will be marked as processed.", eventType, eventId);
        }

        messageLogRepository.markAsConsumed(groupId, eventId, instantProvider.now());
    }

}
