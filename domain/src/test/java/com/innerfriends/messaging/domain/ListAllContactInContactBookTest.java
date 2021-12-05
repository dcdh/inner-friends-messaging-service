package com.innerfriends.messaging.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListAllContactInContactBookTest {

    private ContactBook contactBook;

    @BeforeEach
    public void setup() {
        contactBook = new ContactBook(new Owner(new ContactIdentifier("Mario")), buildCreatedAt(),
                List.of(new Contact(new ContactIdentifier("Peach"), buildAddedAt())),
                1l);
    }

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(ListAllContactInContactBook.class).verify();
    }

    @Test
    public void should_return_contact_book() {
        assertThat(new ListAllContactInContactBook(contactBook).contactBook())
                .isEqualTo(contactBook);
    }

    @Test
    public void should_return_owner() {
        assertThat(new ListAllContactInContactBook(contactBook).owner())
                .isEqualTo(new Owner(new ContactIdentifier("Mario")));
    }

    @Test
    public void should_return_created_at() {
        assertThat(new ListAllContactInContactBook(contactBook).createdAt())
                .isEqualTo(buildCreatedAt());
    }

    @Test
    public void should_return_all_contacts() {
        assertThat(new ListAllContactInContactBook(contactBook).allContacts())
                .isEqualTo(List.of(new Contact(new ContactIdentifier("Peach"), buildAddedAt())));
    }

    @Test
    public void should_return_version() {
        assertThat(new ListAllContactInContactBook(contactBook).version())
                .isEqualTo(1l);
    }

    private CreatedAt buildCreatedAt() {
        return new CreatedAt(
                ZonedDateTime.of(2021, 10, 1, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

    private AddedAt buildAddedAt() {
        return new AddedAt(ZonedDateTime.of(2021, 10, 2, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}