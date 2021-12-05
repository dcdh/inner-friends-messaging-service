package com.innerfriends.messaging.infrastructure.outbox.contactbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.CreatedAt;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.outbox.contactbook.ContactBookCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.approvaltests.Approvals.verifyJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ContactBookCreatedEventTest {

    private InstantProvider instantProvider;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        this.instantProvider = mock(InstantProvider.class);
    }

    @Test
    public void should_return_expected_event() {
        // Given
        final Owner owner = new Owner("Mario");
        final ContactBook contactBook = new ContactBook(owner, buildCreatedAt());
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When
        final ContactBookCreatedEvent contactBookCreatedEvent = ContactBookCreatedEvent.of(contactBook, objectMapper, instantProvider);

        // Then
        assertThat(contactBookCreatedEvent.getAggregateId()).isEqualTo("Mario");
        assertThat(contactBookCreatedEvent.getAggregateType()).isEqualTo("ContactBook");
        assertThat(contactBookCreatedEvent.getTimestamp()).isEqualTo(Instant.ofEpochSecond(1));
        assertThat(contactBookCreatedEvent.getType()).isEqualTo("ContactBookCreated");
        verifyJson(contactBookCreatedEvent.getPayload().toString());
    }

    private CreatedAt buildCreatedAt() {
        return new CreatedAt(
                ZonedDateTime.of(2021, 10, 1, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
