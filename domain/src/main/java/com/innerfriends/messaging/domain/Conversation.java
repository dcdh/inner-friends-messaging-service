package com.innerfriends.messaging.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Conversation extends Aggregate {

    private final ConversationIdentifier conversationIdentifier;
    private final List<ConversationEvent> events;

    public Conversation(final ConversationIdentifier conversationIdentifier,
                        final List<? extends ConversationEvent> events,
                        final Long version) {
        super(version);
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.events = new ArrayList<>(Objects.requireNonNull(events));
        // TODO le premier event doit être un startedBy et je ne dois en avoir qu'un
    }

    public Conversation(final ConversationIdentifier conversationIdentifier,
                        final Message message,
                        final List<ParticipantIdentifier> participantsIdentifier) {
        this(conversationIdentifier,
                Stream.concat(
                        participantsIdentifier.stream()
                                .map(participantIdentifier -> new ParticipantAddedConversationEvent(participantIdentifier,
                                        new AddedAt(message.postedAt()))),
                        Stream.of(new MessagePostedConversationEvent(message))).collect(Collectors.toList()),
                0l);
    }

    public Conversation post(final From from, final PostedAt postedAt, final Content content) {
        if (!hasParticipant(from.identifier())) {
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
        return events.stream()
                .filter(conversationEvent -> ConversationEventType.PARTICIPANT_ADDED.equals(conversationEvent.conversationEventType()))
                .map(ConversationEvent::eventFrom)
                .map(ParticipantIdentifier::new)
                .collect(Collectors.toList());
    }

    public LastInteractionAt lastInteractionAt() {
        return events.stream()
                .reduce((first, seconde) -> seconde)
                .map(ConversationEvent::eventAt)
                .map(LastInteractionAt::new)
                .get();
    }

    public boolean hasParticipant(final ParticipantIdentifier participantIdentifier) {
        return events.stream()
                .filter(conversationEvent -> ConversationEventType.PARTICIPANT_ADDED.equals(conversationEvent.conversationEventType()))
                .map(ConversationEvent::eventFrom)
                .map(ParticipantIdentifier::new)
                .anyMatch(participantIdentifierInConversation -> participantIdentifierInConversation.equals(participantIdentifier));
    }

    public List<ConversationEvent> events() {
        return events.stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation)) return false;
        if (!super.equals(o)) return false;
        final Conversation that = (Conversation) o;
        return Objects.equals(conversationIdentifier, that.conversationIdentifier) &&
                Objects.equals(events, that.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), conversationIdentifier, events);
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationIdentifier=" + conversationIdentifier +
                ", events=" + events +
                "} " + super.toString();
    }
}
