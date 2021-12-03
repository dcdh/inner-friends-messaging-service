package com.innerfriends.messaging.domain;

public interface ConversationEvent {

    ConversationEventType conversationEventType();

    EventFrom eventFrom();

    EventAt eventAt();

    Content content();

    Message toMessage();

}
