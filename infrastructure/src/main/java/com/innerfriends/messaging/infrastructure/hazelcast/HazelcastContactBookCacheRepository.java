package com.innerfriends.messaging.infrastructure.hazelcast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.infrastructure.opentelemetry.NewSpan;
import com.innerfriends.messaging.infrastructure.usecase.cache.ContactBookCacheRepository;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class HazelcastContactBookCacheRepository implements ContactBookCacheRepository {

    private static final Logger LOG = Logger.getLogger(HazelcastContactBookCacheRepository.class);

    public static final String MAP_NAME = "contactBook";

    private final HazelcastInstance hazelcastInstance;

    private final ObjectMapper objectMapper;

    public HazelcastContactBookCacheRepository(final HazelcastInstance hazelcastInstance, final ObjectMapper objectMapper) {
        this.hazelcastInstance = Objects.requireNonNull(hazelcastInstance);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @NewSpan
    @Override
    public void evict(final Owner owner) {
        hazelcastInstance.getMap(MAP_NAME).remove(owner.identifier().identifier());
    }

    @NewSpan
    @Override
    public Optional<ContactBook> getByOwner(final Owner owner) {
        return Optional.ofNullable(hazelcastInstance.getMap(MAP_NAME).get(owner.identifier().identifier()))
                .map(String.class::cast)
                .map(value -> readFromJson(value))
                .map(HazelcastContactBook::contactBook);
    }

    @NewSpan
    @Override
    public void store(final ContactBook contactBook) {
        hazelcastInstance.getMap(MAP_NAME).put(contactBook.owner().identifier().identifier(),
                writeFrom(new HazelcastContactBook(contactBook)));
    }

    private HazelcastContactBook readFromJson(final String json) {
        try {
            return objectMapper.readValue(json, HazelcastContactBook.class);
        } catch (final JsonProcessingException e) {
            LOG.error("Unable to read from json", e);
            throw new RuntimeException(e);
        }
    }

    private String writeFrom(final HazelcastContactBook hazelcastContactBook) {
        try {
            return objectMapper.writeValueAsString(hazelcastContactBook);
        } catch (JsonProcessingException e) {
            LOG.error("Unable to write to HazelcastCachedUserProfilePictures", e);
            throw new RuntimeException(e);
        }
    }

}
