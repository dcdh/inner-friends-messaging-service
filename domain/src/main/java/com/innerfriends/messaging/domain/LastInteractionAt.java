package com.innerfriends.messaging.domain;

import java.time.ZonedDateTime;
import java.util.Objects;

public final class LastInteractionAt {

    private final ZonedDateTime at;

    public LastInteractionAt(final EventAt eventAt) {
        this(eventAt.at());
    }

    public LastInteractionAt(final AddedAt addedAt) {
        this(addedAt.at());
    }

    public LastInteractionAt(final PostedAt postedAt) {
        this(postedAt.at());
    }

    public LastInteractionAt(final ZonedDateTime at) {
        this.at = Objects.requireNonNull(at);
    }

    public ZonedDateTime at() {
        return at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LastInteractionAt)) return false;
        LastInteractionAt that = (LastInteractionAt) o;
        return Objects.equals(at, that.at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at);
    }
}
