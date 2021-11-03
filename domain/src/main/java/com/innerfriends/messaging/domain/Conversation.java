package com.innerfriends.messaging.domain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Conversation extends Aggregate {

    private final ConversationIdentifier conversationIdentifier;
    private final List<Message> messages;
    private final List<ParticipantIdentifier> participantsIdentifier;

    public Conversation(final ConversationIdentifier conversationIdentifier,
                        final List<Message> messages,
                        final List<ParticipantIdentifier> participantsIdentifier,
                        final Long version) {
        super(version);
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.messages = messages.stream()
                .sorted(Comparator.comparing(e -> e.postedAt().at()))
                .collect(Collectors.toList());
        this.participantsIdentifier = Objects.requireNonNull(participantsIdentifier);
    }

    public Conversation(final ConversationIdentifier conversationIdentifier,
                        final Message message,
                        final List<ParticipantIdentifier> participantsIdentifier) {
        this(conversationIdentifier, Arrays.asList(message),
                participantsIdentifier,
                0l);
    }

    public Conversation post(final From from, final PostedAt postedAt, final Content content) {
        if (!this.participantsIdentifier.contains(from.identifier())) {
            throw new YouAreNotAParticipantException(conversationIdentifier, from);
        }
        this.apply(() -> messages.add(new Message(from, postedAt, content)));
        return this;
    }

    public List<Message> messages() {
        return messages.stream().collect(Collectors.toUnmodifiableList());
    }

    public Message lastMessage() {
        return messages.stream()
                .reduce((first, seconde) -> seconde)
                .get();
    }

    public ConversationIdentifier conversationIdentifier() {
        return conversationIdentifier;
    }

    public List<ParticipantIdentifier> participants() {
        return participantsIdentifier;
    }

    public LastInteractionAt lastInteractionAt() {
        return messages.stream()
                .reduce((first, seconde) -> seconde)
                .map(Message::postedAt)
                .map(LastInteractionAt::new)
                .get();
    }

    public boolean hasParticipant(final ParticipantIdentifier participantIdentifier) {
        return this.participantsIdentifier.contains(participantIdentifier);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation)) return false;
        if (!super.equals(o)) return false;
        final Conversation that = (Conversation) o;
        return Objects.equals(conversationIdentifier, that.conversationIdentifier) &&
                Objects.equals(messages, that.messages) &&
                Objects.equals(participantsIdentifier, that.participantsIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), conversationIdentifier, messages, participantsIdentifier);
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationIdentifier=" + conversationIdentifier +
                ", messages=" + messages +
                ", participantsIdentifier=" + participantsIdentifier +
                "} " + super.toString();
    }
}
