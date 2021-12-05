package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListAllContactsUseCaseTest {

    @Test
    public void should_list_all_contacts() {
        // Given
        final List<Contact> contacts = List.of(
                new Contact(new ContactIdentifier("Mario"), buildAddedAt(3)),
                new Contact(new ContactIdentifier("Luigi"), buildAddedAt(2)),
                new Contact(new ContactIdentifier("Peach"), buildAddedAt(1))
        );
        final Owner owner = mock(Owner.class);
        final ContactBook contactBook = new ContactBook(owner, buildCreatedAt(), contacts, 1l);
        final ContactBookRepository contactBookRepository = mock(ContactBookRepository.class);
        final ListAllContactsCommand listAllContactsCommand = new ListAllContactsCommand(owner);
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);
        final ListAllContactsUseCase listAllContactsUseCase = new ListAllContactsUseCase(contactBookRepository);

        // When && Then
        assertThat(listAllContactsUseCase.execute(listAllContactsCommand))
                .isEqualTo(new ListAllContactInContactBook(
                        new ContactBook(owner, buildCreatedAt(), contacts, 1l)
                ));
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

    private CreatedAt buildCreatedAt() {
        return new CreatedAt(
                ZonedDateTime.of(2021, 10, 1, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
