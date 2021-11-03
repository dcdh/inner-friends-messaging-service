package com.innerfriends.messaging.domain;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class Aggregate {

    private Long version;

    public Aggregate(final Long version) {
        this.version = Objects.requireNonNull(version);
        if (version < 0) {
            throw new IllegalStateException();
        }
    }

    protected void apply(final Callable apply) {
        try {
            apply.call();
        } catch (final Exception exception) {
            throw new FailedToMutateAggregateException(exception);
        }
        version++;
    }

    public Long version() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Aggregate)) return false;
        final Aggregate aggregate = (Aggregate) o;
        return Objects.equals(version, aggregate.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Override
    public String toString() {
        return "Aggregate{" +
                "version=" + version +
                '}';
    }
}
