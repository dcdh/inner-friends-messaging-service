package com.innerfriends.messaging.domain;

import java.time.ZonedDateTime;
import java.util.Objects;

public final class AddedAt {

    private final ZonedDateTime at;

    public AddedAt(final EventAt eventAt) {
        this(eventAt.at());
    }

    public AddedAt(final PostedAt postedAt) {
        this(postedAt.at());
    }

    public AddedAt(final ZonedDateTime at) {
        this.at = at;
    }

    public ZonedDateTime at() {
        return at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddedAt)) return false;
        AddedAt addedAt = (AddedAt) o;
        return Objects.equals(at, addedAt.at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at);
    }

    @Override
    public String toString() {
        return "AddedAt{" +
                "at=" + at +
                '}';
    }
}
