package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class EventFrom {

    private final ParticipantIdentifier identifier;

    public EventFrom(final From from) {
        this(from.identifier());
    }

    public EventFrom(final ParticipantIdentifier identifier) {
        this.identifier = Objects.requireNonNull(identifier);
    }

    public ParticipantIdentifier identifier() {
        return identifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof EventFrom)) return false;
        final EventFrom eventFrom = (EventFrom) o;
        return Objects.equals(identifier, eventFrom.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
