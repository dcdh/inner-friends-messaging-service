package com.innerfriends.messaging.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Conversation {

    private final ConversationIdentifier conversationIdentifier;
    private final List<Message> messages;
    private final List<ParticipantIdentifier> participants;

    public Conversation(final ConversationIdentifier conversationIdentifier,
                        final List<Message> messages,
                        final List<ParticipantIdentifier> participants) {
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.messages = messages.stream()
                .sorted(Comparator.comparing(e -> e.postedAt().at()))
                .collect(Collectors.toList());
        this.participants = Objects.requireNonNull(participants);
    }

    public Message post(final From from, final PostedAt postedAt, final Content content) {
        final Message messageSent = new Message(from, postedAt, content);
        messages.add(messageSent);
        return messageSent;
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
        return participants;
    }

    public LastInteractionAt lastInteractionAt() {
        return messages.stream()
                .reduce((first, seconde) -> seconde)
                .map(Message::postedAt)
                .map(LastInteractionAt::new)
                .get();
    }

    public boolean hasParticipant(final ParticipantIdentifier participantIdentifier) {
        return this.participants.contains(participantIdentifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation)) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(conversationIdentifier, that.conversationIdentifier) &&
                Objects.equals(messages, that.messages) &&
                Objects.equals(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationIdentifier, messages, participants);
    }
}
