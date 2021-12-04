package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ConversationIdentifier;
import com.innerfriends.messaging.domain.ParticipantIdentifier;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class AddParticipantIntoConversationCommand implements UseCaseCommand<ConversationIdentifier> {

    private final ConversationIdentifier conversationIdentifier;
    private final ParticipantIdentifier participantIdentifier;

    public AddParticipantIntoConversationCommand(final ConversationIdentifier conversationIdentifier,
                                                 final ParticipantIdentifier participantIdentifier) {
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.participantIdentifier = Objects.requireNonNull(participantIdentifier);
    }

    @Override
    public ConversationIdentifier identifier() {
        return conversationIdentifier;
    }

    public ConversationIdentifier conversationIdentifier() {
        return conversationIdentifier;
    }

    public ParticipantIdentifier participantIdentifier() {
        return participantIdentifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AddParticipantIntoConversationCommand)) return false;
        final AddParticipantIntoConversationCommand that = (AddParticipantIntoConversationCommand) o;
        return Objects.equals(conversationIdentifier, that.conversationIdentifier) &&
                Objects.equals(participantIdentifier, that.participantIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationIdentifier, participantIdentifier);
    }
}
