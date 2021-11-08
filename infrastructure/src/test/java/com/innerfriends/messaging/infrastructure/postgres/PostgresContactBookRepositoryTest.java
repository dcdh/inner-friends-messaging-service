package com.innerfriends.messaging.infrastructure.postgres;

import com.innerfriends.messaging.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class PostgresContactBookRepositoryTest extends RepositoryTesting {

    private static final String CREATE_CONTACT_BOOK_SQL = "INSERT INTO public.T_CONTACT_BOOK VALUES ('Mario', '[]', 0)";
    private static final String COUNT_CONTACT_BOOK_SQL = "SELECT COUNT(*) FROM public.T_CONTACT_BOOK";

    @Inject
    PostgresContactBookRepository postgresContactBookRepository;

    @Test
    public void should_return_contact_book_by_owner() throws Exception {
        // Given
        final Owner owner = new Owner("Mario");
        runInTransaction(() ->
                entityManager.createNativeQuery(CREATE_CONTACT_BOOK_SQL).executeUpdate());

        // When
        final ContactBook contactBook = runInTransaction(() -> postgresContactBookRepository.getByOwner(owner));

        // Then
        assertThat(contactBook).isEqualTo(
                new ContactBook(
                        new Owner("Mario"), Collections.emptyList(), 0l));
    }

    @Test
    public void should_get_contact_book_by_owner_fail_when_owner_does_not_have_a_contact_book() {
        assertThatThrownBy(() -> runInTransaction(() -> postgresContactBookRepository.getByOwner(new Owner("Mario"))))
                .isInstanceOf(NoContactBookFoundException.class);
    }

    @Test
    public void should_save_contact_book() throws Exception {
        // When
        runInTransaction(() -> {
            postgresContactBookRepository.save(new ContactBook(new Owner("Mario"),
                List.of(
                        new Contact(new ContactIdentifier("Peach"), buildAddedAt(1))
                ), 1l));
            return null;
        });

        // Then
        assertThat(((Number) entityManager.createNativeQuery(COUNT_CONTACT_BOOK_SQL).getSingleResult()).longValue()).isEqualTo(1l);
    }

    @Test
    public void should_save_contact_book_twice() throws Exception {
        // Given
        runInTransaction(() ->
                entityManager.createNativeQuery(CREATE_CONTACT_BOOK_SQL).executeUpdate());

        // When
        runInTransaction(() -> {
            postgresContactBookRepository.save(new ContactBook(
                    new Owner("Mario"),
                    List.of(new Contact(new ContactIdentifier("Peach"), buildAddedAt(1))),
                    1l));
            return null;
        });

        // Then
        final ContactBook contactBook = runInTransaction(() -> postgresContactBookRepository.getByOwner(new Owner("Mario")));
        assertThat(contactBook).isEqualTo(new ContactBook(
                new Owner("Mario"),
                List.of(new Contact(new ContactIdentifier("Peach"), buildAddedAt(1))),
                1l));
    }

    @Test
    public void should_fail_when_next_version_is_not_incremented_by_one() throws Exception {
        // Given
        runInTransaction(() -> entityManager.createNativeQuery(CREATE_CONTACT_BOOK_SQL).executeUpdate());
        final ContactBook contactBook = new ContactBook(new Owner("Mario"), emptyList(), 2l);

        // When && Then
        assertThatThrownBy(() -> runInTransaction(() -> {
            postgresContactBookRepository.save(contactBook);
            return null;
        }))
                .getRootCause()
                .isInstanceOf(PSQLException.class)
                .hasMessage("ERROR: ContactBook version unexpected on update for owner Mario - current version 2 - expected version 1\n" +
                        "  Où : PL/pgSQL function contactbook_check_version_on_update() line 6 at RAISE");
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
