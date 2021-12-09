package com.innerfriends.messaging.domain;

import java.util.*;

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
        final ConversationEvent previous = events.get(events.size() - 1);
        this.apply(() -> events.add(new MessagePostedConversationEvent(new Message(from, postedAt, content), previous.participantsIdentifier())));
        return this;
    }

    public Conversation addAParticipantIntoConversation(final ParticipantIdentifier participantIdentifier,
                                                        final AddedAt addedAt) {
        final ConversationEvent previous = events.get(events.size() - 1);
        final List<ParticipantIdentifier> participantIdentifiers = new ArrayList<>(previous.participantsIdentifier());
        participantIdentifiers.add(participantIdentifier);
        this.apply(() -> events.add(new ParticipantAddedConversationEvent(participantIdentifier, addedAt, participantIdentifiers)));
        return this;
    }

    public Participant lastAddedParticipant() {
        return events.stream()
                .filter(conversationEvent -> ConversationEventType.PARTICIPANT_ADDED.equals(conversationEvent.conversationEventType()))
                .reduce((first, seconde) -> seconde)
                .map(Participant::new)
                .get();
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

    // TODO remove reducer by getting the n-1 event to increase perf
    public List<ParticipantIdentifier> participants() {
        return events.stream()
                .reduce((first, seconde) -> seconde)
                .map(ConversationEvent::participantsIdentifier)
                .get();
    }

    // TODO remove reducer by getting the n-1 event to increase perf
    public LastInteractionAt lastInteractionAt() {
        return events.stream()
                .reduce((first, seconde) -> seconde)
                .map(ConversationEvent::eventAt)
                .map(LastInteractionAt::new)
                .get();
    }

    public boolean hasParticipant(final ParticipantIdentifier participantIdentifier) {
        return participants().contains(participantIdentifier);
    }

    public List<ConversationEvent> events() {
        return Collections.unmodifiableList(events);
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
