package com.innerfriends.messaging.infrastructure.hazelcast;

import com.innerfriends.messaging.domain.AddedAt;
import com.innerfriends.messaging.domain.Contact;
import com.innerfriends.messaging.domain.ContactIdentifier;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RegisterForReflection
public final class HazelcastContact {

    public String contactIdentifier;

    public String addedAt;

    public HazelcastContact() {}

    public HazelcastContact(final Contact contact) {
        this.contactIdentifier = contact.contactIdentifier().identifier();
        this.addedAt = contact.addedAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
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
        if (!(o instanceof HazelcastContact)) return false;
        final HazelcastContact that = (HazelcastContact) o;
        return Objects.equals(contactIdentifier, that.contactIdentifier) &&
                Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactIdentifier, addedAt);
    }
}
