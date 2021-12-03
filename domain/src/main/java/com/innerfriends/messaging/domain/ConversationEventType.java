package com.innerfriends.messaging.domain;

public enum ConversationEventType {

    MESSAGE_POSTED {

        @Override
        public ConversationEvent toConversationEvent(final EventFrom eventFrom, final EventAt eventAt, final Content content) {
            return new MessagePostedConversationEvent(new Message(new From(eventFrom), new PostedAt(eventAt), content));
        }

    },

    PARTICIPANT_ADDED {

        @Override
        public ConversationEvent toConversationEvent(final EventFrom eventFrom, final EventAt eventAt, final Content content) {
            return new ParticipantAddedConversationEvent(new ParticipantIdentifier(eventFrom), new AddedAt(eventAt));
        }

    };

    public abstract ConversationEvent toConversationEvent(EventFrom eventFrom, EventAt eventAt, Content content);

}
