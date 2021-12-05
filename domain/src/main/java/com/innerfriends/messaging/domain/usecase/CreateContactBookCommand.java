package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.List;
import java.util.Objects;

public final class CreateContactBookCommand implements UseCaseCommand<Owner> {

    private final Owner owner;
    private final List<ContactIdentifier> contactIdentifiers;

    public CreateContactBookCommand(final Owner owner, final List<ContactIdentifier> contactIdentifiers) {
        this.owner = Objects.requireNonNull(owner);
        this.contactIdentifiers = Objects.requireNonNull(contactIdentifiers);
    }

    public Owner owner() {
        return owner;
    }

    public List<ContactIdentifier> contactIdentifiers() {
        return contactIdentifiers;
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
        return Objects.equals(owner, that.owner) &&
                Objects.equals(contactIdentifiers, that.contactIdentifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, contactIdentifiers);
    }
}
