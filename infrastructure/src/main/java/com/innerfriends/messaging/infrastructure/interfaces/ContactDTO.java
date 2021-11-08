package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.Contact;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.util.Objects;

@RegisterForReflection
public final class ContactDTO {

    private final String contactIdentifier;
    private final ZonedDateTime addedAt;

    public ContactDTO(final Contact contact) {
        this.contactIdentifier = contact.contactIdentifier().identifier();
        this.addedAt = contact.addedAt().at();
    }

    public String getContactIdentifier() {
        return contactIdentifier;
    }

    public ZonedDateTime getAddedAt() {
        return addedAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactDTO)) return false;
        final ContactDTO that = (ContactDTO) o;
        return Objects.equals(contactIdentifier, that.contactIdentifier) &&
                Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactIdentifier, addedAt);
    }
}
