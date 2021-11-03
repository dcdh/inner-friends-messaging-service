package com.innerfriends.messaging.domain;

import java.time.ZonedDateTime;
import java.util.Objects;

public final class OpenedAt {

    private final ZonedDateTime at;

    public OpenedAt(final ZonedDateTime at) {
        this.at = at;
    }

    public ZonedDateTime at() {
        return at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenedAt)) return false;
        OpenedAt openedAt = (OpenedAt) o;
        return Objects.equals(at, openedAt.at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at);
    }
}
