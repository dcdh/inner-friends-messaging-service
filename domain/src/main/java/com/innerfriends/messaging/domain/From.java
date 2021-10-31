package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class From {

    private final ParticipantIdentifier identifier;

    public From(final ParticipantIdentifier identifier) {
        this.identifier = Objects.requireNonNull(identifier);
    }

    public ParticipantIdentifier identifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof From)) return false;
        From from1 = (From) o;
        return Objects.equals(identifier, from1.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "From{" +
                "identifier=" + identifier +
                '}';
    }
}
