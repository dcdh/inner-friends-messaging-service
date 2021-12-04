package com.innerfriends.messaging.domain;

import java.util.Objects;

public final class Content {

    private final String content;

    public Content(final String content) {
        this.content = Objects.requireNonNull(content);
        if (this.content.length() >= 500) {
            throw new IllegalStateException();
        }
    }

    public String content() {
        return content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Content)) return false;
        final Content content1 = (Content) o;
        return Objects.equals(content, content1.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return "Content{" +
                "content='" + content + '\'' +
                '}';
    }
}
