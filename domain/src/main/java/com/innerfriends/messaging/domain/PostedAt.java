package com.innerfriends.messaging.domain;

import java.time.ZonedDateTime;
import java.util.Objects;

public final class PostedAt {

    private final ZonedDateTime at;

    public PostedAt(final ZonedDateTime at) {
        this.at = Objects.requireNonNull(at);
    }

    public ZonedDateTime at() {
        return at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostedAt)) return false;
        PostedAt postedAt = (PostedAt) o;
        return Objects.equals(at, postedAt.at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at);
    }

    @Override
    public String toString() {
        return "PostedAt{" +
                "at=" + at +
                '}';
    }
}
