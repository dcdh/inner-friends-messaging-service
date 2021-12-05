package com.innerfriends.messaging.infrastructure.hazelcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.infrastructure.opentelemetry.OpenTelemetryTracingService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class HazelcastContactBookCacheRepositoryTest {

    @Inject
    ObjectMapper objectMapper;

    @InjectSpy
    HazelcastContactBookCacheRepository hazelcastContactBookCacheRepository;

    @Inject
    HazelcastInstance hazelcastInstance;

    @InjectMock
    OpenTelemetryTracingService openTelemetryTracingService;

    @BeforeEach
    @AfterEach
    public void flush() {
        hazelcastInstance.getMap(HazelcastContactBookCacheRepository.MAP_NAME).clear();
    }

    @Test
    public void should_get_by_owner() throws Exception {
        // Given
        final Owner owner = new Owner("Mario");
        final ContactBook contactBook = new ContactBook(owner, buildCreatedAt(), Collections.emptyList());
        contactBook.addNewContact(new ContactIdentifier("Peach"), buildAddedAt(2));
        final InOrder inOrder = inOrder(hazelcastContactBookCacheRepository, openTelemetryTracingService);
        hazelcastInstance.getMap(HazelcastContactBookCacheRepository.MAP_NAME).put("Mario",
                objectMapper.writeValueAsString(new HazelcastContactBook(contactBook)));

        // When
        final Optional<ContactBook> contactBookRetrieved = hazelcastContactBookCacheRepository.getByOwner(owner);

        // Then
        assertThat(contactBookRetrieved.isPresent()).isTrue();
        assertThat(contactBookRetrieved.get()).isEqualTo(contactBook);
        inOrder.verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        inOrder.verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    @Test
    public void should_not_return_contact_book_when_not_stored_yet() {
        // Given
        final Owner owner = new Owner("Mario");

        // When
        final Optional<ContactBook> contactBookRetrieved = hazelcastContactBookCacheRepository.getByOwner(owner);

        // Then
        assertThat(contactBookRetrieved.isPresent()).isFalse();
    }

    @Test
    public void should_store_contact_identifiers() throws Exception {
        // Given
        final Owner owner = new Owner("Mario");
        final ContactBook contactBook = new ContactBook(owner, buildCreatedAt(), Collections.emptyList());
        contactBook.addNewContact(new ContactIdentifier("Luigi"), buildAddedAt(1));
        contactBook.addNewContact(new ContactIdentifier("Peach"), buildAddedAt(2));
        final InOrder inOrder = inOrder(hazelcastContactBookCacheRepository, openTelemetryTracingService);

        // When
        hazelcastContactBookCacheRepository.store(contactBook);

        // Then
        assertThat(objectMapper.readValue(
                (String) hazelcastInstance.getMap(HazelcastContactBookCacheRepository.MAP_NAME).get("Mario"), HazelcastContactBook.class))
                .isEqualTo(new HazelcastContactBook(contactBook));
        inOrder.verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        inOrder.verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    @Test
    public void should_evict() throws Exception {
        // Given
        final Owner owner = new Owner("Mario");
        final ContactBook contactBook = new ContactBook(owner, buildCreatedAt(), Collections.emptyList());
        contactBook.addNewContact(new ContactIdentifier("Peach"), buildAddedAt(2));
        final InOrder inOrder = inOrder(hazelcastContactBookCacheRepository, openTelemetryTracingService);
        hazelcastInstance.getMap(HazelcastContactBookCacheRepository.MAP_NAME).put("Mario",
                objectMapper.writeValueAsString(new HazelcastContactBook(contactBook)));

        // When
        hazelcastContactBookCacheRepository.evict(owner);

        // Then
        assertThat(hazelcastInstance.getMap(HazelcastContactBookCacheRepository.MAP_NAME).get("Mario")).isNull();
        inOrder.verify(openTelemetryTracingService, times(1)).startANewSpan(any());
        inOrder.verify(openTelemetryTracingService, times(1)).endSpan(any());
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

    private CreatedAt buildCreatedAt() {
        return new CreatedAt(
                ZonedDateTime.of(2021, 10, 1, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
