package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class Owner {

    private final ContactIdentifier identifier;

    public Owner(final ContactIdentifier contactIdentifier) {
        this.identifier = Objects.requireNonNull(contactIdentifier);
    }

    public Owner(final String identifier) {
        this(new ContactIdentifier(identifier));
    }

    public Owner(final OpenedBy openedBy) {
        this(new ContactIdentifier(openedBy.identifier().identifier()));
    }

    public ContactIdentifier identifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner)) return false;
        Owner owner = (Owner) o;
        return Objects.equals(identifier, owner.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "Owner{" +
                "identifier=" + identifier +
                '}';
    }
}
