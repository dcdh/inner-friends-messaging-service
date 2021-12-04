package com.innerfriends.messaging.domain;

import java.util.List;

public interface ConversationEvent {

    ConversationEventType conversationEventType();

    EventFrom eventFrom();

    EventAt eventAt();

    Content content();

    List<ParticipantIdentifier> participantsIdentifier();

    Message toMessage();

}
