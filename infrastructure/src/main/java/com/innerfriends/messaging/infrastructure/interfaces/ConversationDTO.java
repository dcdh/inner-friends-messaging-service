package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.Conversation;
import com.innerfriends.messaging.domain.ParticipantIdentifier;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection
public final class ConversationDTO {

    private final String conversationIdentifier;
    private final List<String> participantsIdentifier;
    private final List<MessageDTO> messages;
    private final Long version;

    public ConversationDTO(final Conversation conversation) {
        this.conversationIdentifier = conversation.conversationIdentifier().identifier();
        this.participantsIdentifier = conversation.participants().stream()
                .map(ParticipantIdentifier::identifier)
                .collect(Collectors.toList());
        this.messages = conversation.messages().stream().map(MessageDTO::new).collect(Collectors.toList());
        this.version = conversation.version();
    }

    public String getConversationIdentifier() {
        return conversationIdentifier;
    }

    public List<String> getParticipantsIdentifier() {
        return participantsIdentifier;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationDTO)) return false;
        final ConversationDTO that = (ConversationDTO) o;
        return Objects.equals(conversationIdentifier, that.conversationIdentifier) &&
                Objects.equals(participantsIdentifier, that.participantsIdentifier) &&
                Objects.equals(messages, that.messages) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationIdentifier, participantsIdentifier, messages, version);
    }
}
