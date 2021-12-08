package com.innerfriends.messaging.infrastructure.postgres;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innerfriends.messaging.domain.AddedAt;
import com.innerfriends.messaging.domain.Contact;
import com.innerfriends.messaging.domain.ContactIdentifier;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RegisterForReflection
public final class ContactEntity {

    @JsonProperty("contactIdentifier")
    public String contactIdentifier;

    @JsonProperty("addedAt")
    public String addedAt;

    public ContactEntity() {}

    public ContactEntity(final Contact contact) {
        this.contactIdentifier = Objects.requireNonNull(contact).contactIdentifier().identifier();
        this.addedAt = Objects.requireNonNull(contact.addedAt()).at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public Contact toContact() {
        return new Contact(
                new ContactIdentifier(contactIdentifier),
                new AddedAt(ZonedDateTime.parse(addedAt, DateTimeFormatter.ISO_ZONED_DATE_TIME))
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactEntity)) return false;
        final ContactEntity that = (ContactEntity) o;
        return Objects.equals(contactIdentifier, that.contactIdentifier) &&
                Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactIdentifier, addedAt);
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "contactIdentifier='" + contactIdentifier + '\'' +
                ", addedAt='" + addedAt + '\'' +
                '}';
    }
}
