package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class Contact {

    private final ContactIdentifier contactIdentifier;
    private final AddedAt addedAt;

    public Contact(final ContactIdentifier contactIdentifier, final AddedAt addedAt) {
        this.contactIdentifier = Objects.requireNonNull(contactIdentifier);
        this.addedAt = Objects.requireNonNull(addedAt);
    }

    public ContactIdentifier contactIdentifier() {
        return contactIdentifier;
    }

    public AddedAt addedAt() {
        return addedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(contactIdentifier, contact.contactIdentifier) &&
                Objects.equals(addedAt, contact.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactIdentifier, addedAt);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactIdentifier=" + contactIdentifier +
                ", addedAt=" + addedAt +
                '}';
    }
}
