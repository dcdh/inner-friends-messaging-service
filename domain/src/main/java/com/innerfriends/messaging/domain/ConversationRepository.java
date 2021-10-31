package com.innerfriends.messaging.domain;

import java.util.List;

public interface ConversationRepository {

    Conversation getConversation(ConversationIdentifier conversationIdentifier);

    void save(Conversation conversation);

    List<Conversation> getConversationsForParticipant(ParticipantIdentifier participantIdentifier);

}
