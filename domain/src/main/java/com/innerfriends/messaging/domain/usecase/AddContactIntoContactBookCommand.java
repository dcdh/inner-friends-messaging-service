package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class AddContactIntoContactBookCommand implements UseCaseCommand<Owner> {

    private final Owner owner;
    private final ContactIdentifier contactIdentifier;

    public AddContactIntoContactBookCommand(final Owner owner,
                                            final ContactIdentifier contactIdentifier) {
        this.owner = Objects.requireNonNull(owner);
        this.contactIdentifier = Objects.requireNonNull(contactIdentifier);
    }

    @Override
    public Owner identifier() {
        return owner;
    }

    public Owner owner() {
        return owner;
    }

    public ContactIdentifier contactIdentifier() {
        return contactIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddContactIntoContactBookCommand)) return false;
        AddContactIntoContactBookCommand that = (AddContactIntoContactBookCommand) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(contactIdentifier, that.contactIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, contactIdentifier);
    }
}
