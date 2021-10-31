package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class ListRecentContactsCommand implements UseCaseCommand<Owner> {

    private final Owner owner;
    private final Integer nbOfContactToReturn;

    public ListRecentContactsCommand(final Owner owner, final Integer nbOfContactToReturn) {
        this.owner = Objects.requireNonNull(owner);
        this.nbOfContactToReturn = Objects.requireNonNull(nbOfContactToReturn);
    }

    @Override
    public Owner identifier() {
        return owner;
    }

    public Owner owner() {
        return owner;
    }

    public Integer nbOfContactToReturn() {
        return nbOfContactToReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListRecentContactsCommand)) return false;
        ListRecentContactsCommand that = (ListRecentContactsCommand) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(nbOfContactToReturn, that.nbOfContactToReturn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, nbOfContactToReturn);
    }
}
