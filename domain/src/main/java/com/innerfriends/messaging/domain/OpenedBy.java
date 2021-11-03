package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class OpenedBy {

    private final ParticipantIdentifier identifier;

    public OpenedBy(final ParticipantIdentifier identifier) {
        this.identifier = Objects.requireNonNull(identifier);
    }

    public ParticipantIdentifier identifier() {
        return identifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenedBy)) return false;
        final OpenedBy openedBy = (OpenedBy) o;
        return Objects.equals(identifier, openedBy.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
