package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Content;

import java.util.Objects;

public class TestContent implements Content {

    private final String content;

    public TestContent(final String content) {
        this.content = content;
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestContent)) return false;
        TestContent that = (TestContent) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return "TestContent{" +
                "content='" + content + '\'' +
                '}';
    }
}
