package com.innerfriends.messaging.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Conversation extends Aggregate {

    private final ConversationIdentifier conversationIdentifier;
    private final List<ConversationEvent> events;

    public Conversation(final ConversationIdentifier conversationIdentifier,
                        final List<? extends ConversationEvent> events,
                        final Long version) {
        super(version);
        this.conversationIdentifier = Objects.requireNonNull(conversationIdentifier);
        this.events = new ArrayList<>(Objects.requireNonNull(events));
        if (!ConversationEventType.STARTED.equals(this.events.get(0).conversationEventType())) {
            throw new IllegalStateException("First event must be the started by one !");
        }
        if (this.events.stream().filter(event -> ConversationEventType.STARTED.equals(event.conversationEventType())).count() > 1) {
            throw new IllegalStateException("Only one started by event is expected !");
        }
        // Je devrais checker que c'est bien sorted by date ...
    }

    public Conversation(final ConversationIdentifier conversationIdentifier,
                        final Message message,
                        final List<ParticipantIdentifier> participantsIdentifier) {
        this(conversationIdentifier,
                List.of(new StartedConversationEvent(message, participantsIdentifier)),
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
                .filter(conversationEvent -> Arrays.asList(ConversationEventType.STARTED, ConversationEventType.MESSAGE_POSTED)
                        .contains(conversationEvent.conversationEventType()))
                .map(ConversationEvent::toMessage)
                .collect(Collectors.toUnmodifiableList());
    }

    public Message lastMessage() {
        return events.stream()
                .filter(conversationEvent -> Arrays.asList(ConversationEventType.STARTED, ConversationEventType.MESSAGE_POSTED)
                        .contains(conversationEvent.conversationEventType()))
                .reduce((first, seconde) -> seconde)
                .map(ConversationEvent::toMessage)
                .get();
    }

    public ConversationIdentifier conversationIdentifier() {
        return conversationIdentifier;
    }

    public List<ParticipantIdentifier> participants() {
        return events.stream()
                .flatMap(event -> event.participantsIdentifier().stream())
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
                .flatMap(event -> event.participantsIdentifier().stream())
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
