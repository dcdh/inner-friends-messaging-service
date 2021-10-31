package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class ToIsNotInContactBookException extends RuntimeException {

    private final From from;
    private final To to;

    public ToIsNotInContactBookException(final From from, final To to) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
    }

    public From from() {
        return from;
    }

    public To to() {
        return to;
    }
}
