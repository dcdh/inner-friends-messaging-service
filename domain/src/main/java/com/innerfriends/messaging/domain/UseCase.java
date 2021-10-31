package com.innerfriends.messaging.domain;

public interface UseCase<R, C extends UseCaseCommand> {

    R execute(C command);

}
