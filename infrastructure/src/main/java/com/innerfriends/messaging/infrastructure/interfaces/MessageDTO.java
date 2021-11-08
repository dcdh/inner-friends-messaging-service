package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.Message;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.util.Objects;

@RegisterForReflection
public final class MessageDTO {

    private final String from;
    private final ZonedDateTime postedAt;
    private final String content;

    public MessageDTO(final Message message) {
        this.from = message.from().identifier().identifier();
        this.postedAt = message.postedAt().at();
        this.content = message.content().content();
    }

    public String getFrom() {
        return from;
    }

    public ZonedDateTime getPostedAt() {
        return postedAt;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageDTO)) return false;
        final MessageDTO that = (MessageDTO) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(postedAt, that.postedAt) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, postedAt, content);
    }
}
