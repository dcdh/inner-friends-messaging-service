package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class To {

    private final ParticipantIdentifier to;

    public To(final ParticipantIdentifier to) {
        this.to = Objects.requireNonNull(to);
    }

    public ParticipantIdentifier identifier() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof To)) return false;
        To to = (To) o;
        return Objects.equals(this.to, to.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to);
    }
}
