package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.Content;
import com.innerfriends.messaging.domain.From;
import com.innerfriends.messaging.domain.Message;
import com.innerfriends.messaging.domain.PostedAt;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RegisterForReflection
public final class MessageEntity {

    public String from;

    public String postedAt;

    public String content;

    public MessageEntity() {}

    public MessageEntity(final Message message) {
        this.from = message.from().identifier().identifier();
        this.postedAt = message.postedAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        this.content = message.content().content();
    }

    public Message toMessage() {
        return new Message(
                new From(from),
                new PostedAt(ZonedDateTime.parse(postedAt, DateTimeFormatter.ISO_ZONED_DATE_TIME)),
                new Content(content));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageEntity)) return false;
        final MessageEntity that = (MessageEntity) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(postedAt, that.postedAt) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, postedAt, content);
    }
}
