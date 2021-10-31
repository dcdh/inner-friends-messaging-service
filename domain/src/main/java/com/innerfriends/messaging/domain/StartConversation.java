package com.innerfriends.messaging.domain;

public interface StartConversation {

    From from();

    To to();

    StartedAt startAt();

    Content content();

}
