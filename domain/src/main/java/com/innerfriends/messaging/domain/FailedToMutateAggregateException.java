package com.innerfriends.messaging.domain;

public class FailedToMutateAggregateException extends RuntimeException {

    public FailedToMutateAggregateException(final Throwable cause) {
        super(cause);
    }
}
