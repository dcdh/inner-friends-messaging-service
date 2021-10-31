package com.innerfriends.messaging.domain;

public interface ConversationIdentifierProvider {

    ConversationIdentifier generate(From from);

}
