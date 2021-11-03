package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class ContactIdentifier {

    private final ParticipantIdentifier identifier;

    public ContactIdentifier(final ParticipantIdentifier identifier) {
        this.identifier = Objects.requireNonNull(identifier);
    }

    public boolean isEqualTo(final ParticipantIdentifier participantIdentifier) {
        return this.identifier.equals(participantIdentifier);
    }

    public ParticipantIdentifier identifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactIdentifier)) return false;
        ContactIdentifier that = (ContactIdentifier) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "ContactIdentifier{" +
                "identifier=" + identifier +
                '}';
    }
}
