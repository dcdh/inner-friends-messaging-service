package com.innerfriends.messaging.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ContactBook extends Aggregate {

    private final Owner owner;
    private final CreatedAt createdAt;
    private final List<Contact> contacts;

    public ContactBook(final Owner owner,
                       final CreatedAt createdAt,
                       final List<Contact> contacts,
                       final Long version) {
        super(version);
        this.owner = Objects.requireNonNull(owner);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.contacts = Objects.requireNonNull(contacts);
    }

    public ContactBook(final Owner owner, final CreatedAt createdAt) {
        this(owner, createdAt, new ArrayList<>(), 0l);
    }

    public ContactBook addNewContact(final ContactIdentifier contactIdentifier, final AddedAt addedAt) {
        this.apply(() -> this.contacts.add(new Contact(contactIdentifier, addedAt)));
        return this;
    }

    public List<ContactIdentifier> recentContacts(final Integer nbOfContactToReturn) {
        return contacts.stream()
                .sorted((e1, e2) -> e2.addedAt().at().compareTo(e1.addedAt().at()))
                .limit(nbOfContactToReturn)
                .map(Contact::contactIdentifier)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Contact> allContacts() {
        return Collections.unmodifiableList(contacts);
    }

    public Contact getLastAddedContact() {
        return contacts.stream().reduce((first, second) -> second).get();
    }

    public boolean hasContact(final ParticipantIdentifier participantIdentifier) {
        return contacts.stream()
                .anyMatch(contact -> contact.contactIdentifier().isEqualTo(participantIdentifier));
    }

    public Owner owner() {
        return owner;
    }

    public CreatedAt createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactBook)) return false;
        if (!super.equals(o)) return false;
        final ContactBook that = (ContactBook) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(contacts, that.contacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), owner, createdAt, contacts);
    }

    @Override
    public String toString() {
        return "ContactBook{" +
                "owner=" + owner +
                ", createdAt=" + createdAt +
                ", contacts=" + contacts +
                "} " + super.toString();
    }
}
