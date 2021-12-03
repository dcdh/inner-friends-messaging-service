package com.innerfriends.messaging.domain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Conversation extends Aggregate {

    private final ConversationIdentifier conversationIdentifier;
    private final List<ConversationEvent> events;
    private final List<ParticipantIdentifier> participantsIdentifier;

    public Conversation(final ConversationIdentifier conversationIdentifier,
                        final List<Message> messages,
                        final List<ParticipantIdentifier> participantsIdentifier,
                        final Long version) {
        super(version);
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.events = messages.stream()
                .sorted(Comparator.comparing(e -> e.postedAt().at()))
                .map(MessagePostedConversationEvent::new)
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
        this.apply(() -> events.add(new MessagePostedConversationEvent(new Message(from, postedAt, content))));
        return this;
    }

    @Deprecated// should be conversationEvents
    public List<Message> messages() {
        return events.stream()
                .filter(conversationEvent -> ConversationEventType.MESSAGE_POSTED.equals(conversationEvent.conversationEventType()))
                .map(ConversationEvent::toMessage)
                .collect(Collectors.toUnmodifiableList());
    }

    public Message lastMessage() {
        return events.stream()
                .filter(conversationEvent -> ConversationEventType.MESSAGE_POSTED.equals(conversationEvent.conversationEventType()))
                .reduce((first, seconde) -> seconde)
                .map(ConversationEvent::toMessage)
                .get();
    }

    public ConversationIdentifier conversationIdentifier() {
        return conversationIdentifier;
    }

    public List<ParticipantIdentifier> participants() {
        return participantsIdentifier;
    }

    public LastInteractionAt lastInteractionAt() {
        return events.stream()
                .reduce((first, seconde) -> seconde)
                .map(ConversationEvent::eventAt)
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
                Objects.equals(events, that.events) &&
                Objects.equals(participantsIdentifier, that.participantsIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), conversationIdentifier, events, participantsIdentifier);
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationIdentifier=" + conversationIdentifier +
                ", events=" + events +
                ", participantsIdentifier=" + participantsIdentifier +
                "} " + super.toString();
    }
}
