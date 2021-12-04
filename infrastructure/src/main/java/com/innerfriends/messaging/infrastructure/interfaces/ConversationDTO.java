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
    private final List<ConversationEventDTO> events;
    private final Long version;

    public ConversationDTO(final Conversation conversation) {
        this.conversationIdentifier = conversation.conversationIdentifier().identifier();
        this.participantsIdentifier = conversation.participants().stream()
                .map(ParticipantIdentifier::identifier)
                .collect(Collectors.toList());
        this.events = conversation.events().stream().map(ConversationEventDTO::new).collect(Collectors.toList());
        this.version = conversation.version();
    }

    public String getConversationIdentifier() {
        return conversationIdentifier;
    }

    public List<String> getParticipantsIdentifier() {
        return participantsIdentifier;
    }

    public List<ConversationEventDTO> getEvents() {
        return events;
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
                Objects.equals(events, that.events) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationIdentifier, participantsIdentifier, events, version);
    }
}
