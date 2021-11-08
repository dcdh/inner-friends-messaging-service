package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.ParticipantIdentifier;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"conversationIdentifier", "version"}),
        name = "T_CONVERSATION"
)
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public final class ConversationEntity {

    @Id
    @NotNull
    public String conversationIdentifier;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    public List<String> participantIdentifiers;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    public List<MessageEntity> messages;

    @NotNull
    public Long version;

    public ConversationEntity() {}

    public ConversationEntity(final Conversation conversation) {
        this.conversationIdentifier = conversation.conversationIdentifier().identifier();
        this.participantIdentifiers = conversation.participants()
                .stream()
                .map(ParticipantIdentifier::identifier)
                .collect(Collectors.toList());
        this.messages = conversation.messages().stream()
                .map(MessageEntity::new)
                .collect(Collectors.toList());
        this.version = conversation.version();
    }

    public Conversation toConversation() {
        return new Conversation(
                new ConversationIdentifier(conversationIdentifier),
                messages.stream()
                        .map(MessageEntity::toMessage)
                        .collect(Collectors.toList()),
                participantIdentifiers.stream()
                        .map(ParticipantIdentifier::new)
                        .collect(Collectors.toList()),
                version);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationEntity)) return false;
        final ConversationEntity that = (ConversationEntity) o;
        return Objects.equals(conversationIdentifier, that.conversationIdentifier) &&
                Objects.equals(participantIdentifiers, that.participantIdentifiers) &&
                Objects.equals(messages, that.messages) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationIdentifier, participantIdentifiers, messages, version);
    }
}
