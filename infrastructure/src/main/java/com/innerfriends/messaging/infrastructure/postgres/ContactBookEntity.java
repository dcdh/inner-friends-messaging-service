package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.CreatedAt;
import com.innerfriends.messaging.domain.Owner;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"owner", "version"}),
        name = "T_CONTACT_BOOK"
)
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class ContactBookEntity {

    @Id
    @NotNull
    private String owner;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private String zoneId;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<ContactEntity> contacts;

    @NotNull
    private Long version;

    public ContactBookEntity() {}

    public ContactBookEntity(final ContactBook contactBook) {
        this.owner = contactBook.owner().identifier().identifier();
        this.createdAt = contactBook.createdAt().at().toOffsetDateTime();
        this.zoneId = contactBook.createdAt().at().getZone().getId();
        this.contacts = contactBook.allContacts()
                .stream()
                .map(ContactEntity::new)
                .collect(Collectors.toList());
        this.version = contactBook.version();
    }

    public ContactBook toContactBook() {
        return new ContactBook(
                new Owner(new ContactIdentifier(owner)),
                new CreatedAt(ZonedDateTime.ofInstant(createdAt.toLocalDateTime(), createdAt.getOffset(), ZoneId.of(zoneId))),
                contacts.stream()
                        .map(ContactEntity::toContact)
                        .collect(Collectors.toList()),
                version);
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactBookEntity)) return false;
        final ContactBookEntity that = (ContactBookEntity) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(zoneId, that.zoneId) &&
                Objects.equals(contacts, that.contacts) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, createdAt, zoneId, contacts, version);
    }
}
