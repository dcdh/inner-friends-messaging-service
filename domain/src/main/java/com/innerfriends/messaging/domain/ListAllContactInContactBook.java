package com.innerfriends.messaging.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ListAllContactInContactBook {

    private final ContactBook contactBook;

    public ListAllContactInContactBook(final ContactBook contactBook) {
        this.contactBook = Objects.requireNonNull(contactBook);
    }

    public ContactBook contactBook() {
        return contactBook;
    }

    public Owner owner() {
        return contactBook.owner();
    }

    public List<Contact> allContacts() {
        return this.contactBook.allContacts()
                .stream()
                .sorted(Comparator.comparing(e -> e.contactIdentifier().identifier()))
                .collect(Collectors.toUnmodifiableList());
    }

    public Long version() {
        return contactBook.version();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ListAllContactInContactBook)) return false;
        final ListAllContactInContactBook that = (ListAllContactInContactBook) o;
        return Objects.equals(contactBook, that.contactBook);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactBook);
    }

    @Override
    public String toString() {
        return "ListAllContactInContactBook{" +
                "contactBook=" + contactBook +
                '}';
    }
}
