package com.innerfriends.messaging.domain;

import java.time.ZonedDateTime;
import java.util.Objects;

public final class EventAt {

    private final ZonedDateTime at;

    public EventAt(final AddedAt addedAt) {
        this(addedAt.at());
    }

    public EventAt(final PostedAt postedAt) {
        this(postedAt.at());
    }

    public EventAt(final ZonedDateTime at) {
        this.at = Objects.requireNonNull(at);
    }

    public ZonedDateTime at() {
        return at;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof EventAt)) return false;
        final EventAt eventAt = (EventAt) o;
        return Objects.equals(at, eventAt.at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at);
    }
}
