package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class Owner {

    private final ParticipantIdentifier identifier;

    public Owner(final OpenedBy openedBy) {
        this(openedBy.identifier());
    }

    public Owner(final ContactIdentifier contactIdentifier) {
        this(contactIdentifier.identifier());
    }

    public Owner(final ParticipantIdentifier identifier) {
        this.identifier = Objects.requireNonNull(identifier);
    }

    public ParticipantIdentifier identifier() {
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
