package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class Message {

    private final From from;
    private final PostedAt postedAt;
    private final Content content;

    public Message(final From from, final PostedAt postedAt, final Content content) {
        this.from = Objects.requireNonNull(from);
        this.postedAt = Objects.requireNonNull(postedAt);
        this.content = Objects.requireNonNull(content);
    }

    public From from() {
        return from;
    }

    public PostedAt postedAt() {
        return postedAt;
    }

    public Content content() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return Objects.equals(from, message.from) &&
                Objects.equals(postedAt, message.postedAt) &&
                Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, postedAt, content);
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", postedAt=" + postedAt +
                ", content=" + content +
                '}';
    }
}
