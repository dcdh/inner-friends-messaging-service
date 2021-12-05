package com.innerfriends.messaging.infrastructure.outbox.contactbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innerfriends.messaging.domain.*;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.outbox.contactbook.ContactAddedIntoContactBookEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.approvaltests.Approvals.verifyJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ContactAddedIntoContactBookEventTest {

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
        final ContactBook contactBook = new ContactBook(owner, buildCreatedAt(),
                List.of(new Contact(new ContactIdentifier("DamDamDeo"), new AddedAt(buildAddedAt(1).at()))));
        contactBook.addNewContact(new ContactIdentifier("Luigi"), buildAddedAt(1));
        contactBook.addNewContact(new ContactIdentifier("Peach"), buildAddedAt(2));
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When
        final ContactAddedIntoContactBookEvent contactAddedIntoContactBookEvent =
                ContactAddedIntoContactBookEvent.of(contactBook, objectMapper, instantProvider);

        // Then
        assertThat(contactAddedIntoContactBookEvent.getAggregateId()).isEqualTo("Mario");
        assertThat(contactAddedIntoContactBookEvent.getAggregateType()).isEqualTo("ContactBook");
        assertThat(contactAddedIntoContactBookEvent.getTimestamp()).isEqualTo(Instant.ofEpochSecond(1));
        assertThat(contactAddedIntoContactBookEvent.getType()).isEqualTo("ContactAddedIntoContactBook");
        verifyJson(contactAddedIntoContactBookEvent.getPayload().toString());
    }

    private CreatedAt buildCreatedAt() {
        return new CreatedAt(
                ZonedDateTime.of(2021, 10, 1, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
