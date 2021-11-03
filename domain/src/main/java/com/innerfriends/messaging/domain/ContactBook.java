package com.innerfriends.messaging.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ContactBook {

    private final Owner owner;
    private final List<Contact> contacts;

    public ContactBook(final Owner owner, final List<Contact> contacts) {
        this.owner = Objects.requireNonNull(owner);
        this.contacts = Objects.requireNonNull(contacts);
    }

    public ContactBook(final Owner owner) {
        this(owner, new ArrayList<>());
    }

    public void addNewContact(final ContactIdentifier contactIdentifier, final AddedAt addedAt) {
        this.contacts.add(new Contact(contactIdentifier, addedAt));
    }

    public List<ContactIdentifier> recentContacts(final Integer nbOfContactToReturn) {
        return contacts.stream()
                .sorted((e1, e2) -> e2.addedAt().at().compareTo(e1.addedAt().at()))
                .limit(nbOfContactToReturn)
                .map(Contact::contactIdentifier)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ContactIdentifier> allContacts() {
        return contacts.stream()
                .sorted(Comparator.comparing(e -> e.contactIdentifier().identifier().identifier()))
                .map(Contact::contactIdentifier)
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean hasContact(final ParticipantIdentifier participantIdentifier) {
        return contacts.stream()
                .anyMatch(contact -> contact.contactIdentifier().isEqualTo(participantIdentifier));
    }

    public Owner owner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactBook)) return false;
        ContactBook that = (ContactBook) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(contacts, that.contacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, contacts);
    }

    @Override
    public String toString() {
        return "ContactBook{" +
                "owner=" + owner +
                ", contacts=" + contacts +
                '}';
    }
}
