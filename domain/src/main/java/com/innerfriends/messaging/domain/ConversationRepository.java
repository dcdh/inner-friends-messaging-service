package com.innerfriends.messaging.domain;

import java.util.List;

public interface ConversationRepository {

    Conversation getConversation(ConversationIdentifier conversationIdentifier) throws UnknownConversationException;

    void createConversation(Conversation conversation);

    List<Conversation> getConversationsForParticipant(ParticipantIdentifier participantIdentifier);

    void saveConversation(Conversation conversation);

}
