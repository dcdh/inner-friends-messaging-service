package com.innerfriends.messaging.infrastructure.hazelcast;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.Owner;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection
public final class HazelcastContactBook {

    public String owner;

    public List<HazelcastContact> contacts;

    public Long version;

    public HazelcastContactBook() {
        this.contacts = new ArrayList<>();
    }

    public HazelcastContactBook(final ContactBook contactBook) {
        this.owner = contactBook.owner().identifier().identifier();
        this.contacts = contactBook.allContacts()
                .stream()
                .map(HazelcastContact::new)
                .collect(Collectors.toList());
        this.version = contactBook.version();
    }

    public ContactBook contactBook() {
        return new ContactBook(
                new Owner(owner),
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
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, contacts, version);
    }
}
