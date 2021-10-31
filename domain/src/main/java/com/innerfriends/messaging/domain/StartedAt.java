package com.innerfriends.messaging.domain;

import java.time.ZonedDateTime;
import java.util.Objects;

public final class StartedAt {

    private final ZonedDateTime at;

    public StartedAt(final ZonedDateTime at) {
        this.at = at;
    }

    public ZonedDateTime at() {
        return at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StartedAt)) return false;
        StartedAt startedAt = (StartedAt) o;
        return Objects.equals(at, startedAt.at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at);
    }
}
