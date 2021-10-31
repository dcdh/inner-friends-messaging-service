package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class ListAllContactsCommand implements UseCaseCommand<Owner> {

    private final Owner owner;

    public ListAllContactsCommand(final Owner owner) {
        this.owner = Objects.requireNonNull(owner);
    }

    @Override
    public Owner identifier() {
        return owner;
    }

    public Owner owner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListAllContactsCommand)) return false;
        ListAllContactsCommand that = (ListAllContactsCommand) o;
        return Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }
}

