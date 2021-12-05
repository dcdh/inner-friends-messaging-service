package com.innerfriends.messaging.domain;

import java.time.ZonedDateTime;
import java.util.Objects;

public final class CreatedAt {

    private final ZonedDateTime at;

    public CreatedAt(final ZonedDateTime at) {
        this.at = Objects.requireNonNull(at);
    }

    public ZonedDateTime at() {
        return at;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CreatedAt)) return false;
        final CreatedAt createdAt = (CreatedAt) o;
        return Objects.equals(at, createdAt.at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at);
    }

    @Override
    public String toString() {
        return "CreatedAt{" +
                "at=" + at +
                '}';
    }
}
