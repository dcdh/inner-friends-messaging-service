package com.innerfriends.messaging.infrastructure.hazelcast;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.CreatedAt;
import com.innerfriends.messaging.domain.Owner;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection
public final class HazelcastContactBook {

    public String owner;

    public List<HazelcastContact> contacts;

    public String createdAt;

    public Long version;

    public HazelcastContactBook() {
        this.contacts = new ArrayList<>();
    }

    public HazelcastContactBook(final ContactBook contactBook) {
        this.owner = contactBook.owner().identifier().identifier();
        this.createdAt = contactBook.createdAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        this.contacts = contactBook.allContacts()
                .stream()
                .map(HazelcastContact::new)
                .collect(Collectors.toList());
        this.version = contactBook.version();
    }

    public ContactBook contactBook() {
        return new ContactBook(
                new Owner(owner),
                new CreatedAt(ZonedDateTime.parse(createdAt, DateTimeFormatter.ISO_ZONED_DATE_TIME)),
                contacts.stream().map(HazelcastContact::toContact).collect(Collectors.toList()),
                version
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof HazelcastContactBook)) return false;
        final HazelcastContactBook that = (HazelcastContactBook) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(contacts, that.contacts) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, contacts, createdAt, version);
    }
}
