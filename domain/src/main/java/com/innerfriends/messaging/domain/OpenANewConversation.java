package com.innerfriends.messaging.domain;

import java.util.List;

public interface OpenANewConversation {

    OpenedBy openedBy();

    List<ParticipantIdentifier> participantsIdentifier();

    OpenedAt startedAt();

    Content content();

}
