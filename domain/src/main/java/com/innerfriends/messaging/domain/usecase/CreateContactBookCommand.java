package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class CreateContactBookCommand implements UseCaseCommand<Owner> {

    private final Owner owner;

    public CreateContactBookCommand(final Owner owner) {
        this.owner = Objects.requireNonNull(owner);
    }

    public Owner owner() {
        return owner;
    }

    @Override
    public Owner identifier() {
        return owner;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateContactBookCommand)) return false;
        final CreateContactBookCommand that = (CreateContactBookCommand) o;
        return Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }
}
