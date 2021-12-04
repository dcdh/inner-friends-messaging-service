package com.innerfriends.messaging.domain;

import java.util.List;

public enum ConversationEventType {

    MESSAGE_POSTED {

        @Override
        public ConversationEvent toConversationEvent(final EventFrom eventFrom, final EventAt eventAt, final Content content,
                                                     final List<ParticipantIdentifier> participantsIdentifier) {
            return new MessagePostedConversationEvent(new Message(new From(eventFrom), new PostedAt(eventAt), content));
        }

    },

    PARTICIPANT_ADDED {

        @Override
        public ConversationEvent toConversationEvent(final EventFrom eventFrom, final EventAt eventAt, final Content content,
                                                     final List<ParticipantIdentifier> participantsIdentifier) {
            return new ParticipantAddedConversationEvent(new ParticipantIdentifier(eventFrom), new AddedAt(eventAt));
        }

    },

    STARTED {

        @Override
        public ConversationEvent toConversationEvent(final EventFrom eventFrom, final EventAt eventAt, final Content content,
                                                     final List<ParticipantIdentifier> participantsIdentifier) {
            return new StartedConversationEvent(new Message(new From(eventFrom), new PostedAt(eventAt), content), participantsIdentifier);
        }

    };

    public abstract ConversationEvent toConversationEvent(EventFrom eventFrom, EventAt eventAt, Content content, List<ParticipantIdentifier> participantsIdentifier);

}
