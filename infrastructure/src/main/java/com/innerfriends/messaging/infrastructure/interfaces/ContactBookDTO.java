package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.ListAllContactInContactBook;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection
public class ContactBookDTO {

    private final String owner;
    private final String createdAt;
    private final List<ContactDTO> contacts;
    private final Long version;

    public ContactBookDTO(final ListAllContactInContactBook listAllContactInContactBook) {
        this.owner = listAllContactInContactBook.owner().identifier().identifier();
        this.createdAt = listAllContactInContactBook.createdAt().at().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        this.contacts = listAllContactInContactBook.allContacts()
                .stream()
                .map(ContactDTO::new)
                .collect(Collectors.toUnmodifiableList());
        this.version = listAllContactInContactBook.version();
    }

    public String getOwner() {
        return owner;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactBookDTO)) return false;
        final ContactBookDTO that = (ContactBookDTO) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(contacts, that.contacts) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, createdAt, contacts, version);
    }
}
