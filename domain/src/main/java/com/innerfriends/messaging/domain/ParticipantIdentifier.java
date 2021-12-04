package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class ParticipantIdentifier {

    private final String identifier;

    public ParticipantIdentifier(final EventFrom eventFrom) {
        this(eventFrom.identifier().identifier);
    }

    public ParticipantIdentifier(final String identifier) {
        this.identifier = Objects.requireNonNull(identifier);
    }

    public String identifier() {
        return identifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipantIdentifier)) return false;
        final ParticipantIdentifier that = (ParticipantIdentifier) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "ParticipantIdentifier{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
