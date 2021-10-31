package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.UseCaseCommand;

import java.util.Objects;

public final class CreateContactBookCommand implements UseCaseCommand<ContactIdentifier> {

    private final ContactIdentifier contactIdentifier;

    public CreateContactBookCommand(final ContactIdentifier contactIdentifier) {
        this.contactIdentifier = Objects.requireNonNull(contactIdentifier);
    }

    public ContactIdentifier contactIdentifier() {
        return contactIdentifier;
    }

    @Override
    public ContactIdentifier identifier() {
        return contactIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateContactBookCommand)) return false;
        CreateContactBookCommand that = (CreateContactBookCommand) o;
        return Objects.equals(contactIdentifier, that.contactIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactIdentifier);
    }
}
