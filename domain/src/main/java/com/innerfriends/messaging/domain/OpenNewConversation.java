package com.innerfriends.messaging.domain;

import java.util.List;

public interface OpenNewConversation {

    OpenedBy openedBy();

    List<ParticipantIdentifier> participantsIdentifier();

    Content content();

}
