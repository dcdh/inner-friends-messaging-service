package com.innerfriends.messaging.domain;

import java.util.List;

public interface ConversationRepository {

    Conversation getConversation(ConversationIdentifier conversationIdentifier);

    void createConversation(Conversation conversation);

    List<Conversation> getConversationsForParticipant(ParticipantIdentifier participantIdentifier);

    void createConversation(Message message);

}
