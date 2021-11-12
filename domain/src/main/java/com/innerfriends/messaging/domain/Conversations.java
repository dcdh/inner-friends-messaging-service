package com.innerfriends.messaging.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Conversations {

    private final List<Conversation> conversations;

    public Conversations(final ParticipantIdentifier participantIdentifier,
                         final List<Conversation> conversations) {
        this.conversations = Objects.requireNonNull(conversations);
        if (this.conversations.size() > 0 && this.conversations.stream()
                .allMatch(conversation -> !conversation.hasParticipant(participantIdentifier))) {
            throw new IllegalStateException("Conversations does not belong to the participant !");
        }
    }

    public List<Conversation> listByLastInteraction() {
        return this.conversations
                .stream()
                .sorted((c1, c2) -> c2.lastInteractionAt().at().compareTo(c1.lastInteractionAt().at()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversations)) return false;
        Conversations that = (Conversations) o;
        return Objects.equals(conversations, that.conversations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversations);
    }
}
