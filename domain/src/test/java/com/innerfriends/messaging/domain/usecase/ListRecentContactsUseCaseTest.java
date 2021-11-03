package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.BeforeEach;
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
public class ListRecentContactsUseCaseTest {

    private List<Contact> contacts;

    @BeforeEach
    public void setup() {
        contacts = List.of(
                new Contact(new ContactIdentifier(new ParticipantIdentifier("Peach")), buildAddedAt(1)),
                new Contact(new ContactIdentifier(new ParticipantIdentifier("Mario")), buildAddedAt(3)),
                new Contact(new ContactIdentifier(new ParticipantIdentifier("Luigi")), buildAddedAt(2))
        );
    }

    @Test
    public void should_list_recent_contacts() {
        // Given
        final Owner owner = mock(Owner.class);
        final ContactBook contactBook = new ContactBook(owner, contacts, 3l);
        final ContactBookRepository contactBookRepository = mock(ContactBookRepository.class);
        final ListRecentContactsCommand listRecentContactsCommand = new ListRecentContactsCommand(owner, 5);
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);
        final ListRecentContactsUseCase listRecentContactsUseCase = new ListRecentContactsUseCase(contactBookRepository);

        // When && Then
        assertThat(listRecentContactsUseCase.execute(listRecentContactsCommand))
                .containsExactly(
                        new ContactIdentifier(new ParticipantIdentifier("Mario")),
                        new ContactIdentifier(new ParticipantIdentifier("Luigi")),
                        new ContactIdentifier(new ParticipantIdentifier("Peach")));
    }

    @Test
    public void should_return_expected_number_of_contacts() {
        // Given
        final Owner owner = mock(Owner.class);
        final ContactBook contactBook = new ContactBook(owner, contacts, 3l);
        final ContactBookRepository contactBookRepository = mock(ContactBookRepository.class);
        final ListRecentContactsCommand listRecentContactsCommand = new ListRecentContactsCommand(owner, 2);
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);
        final ListRecentContactsUseCase listRecentContactsUseCase = new ListRecentContactsUseCase(contactBookRepository);

        // When && Then
        assertThat(listRecentContactsUseCase.execute(listRecentContactsCommand))
                .containsExactly(
                        new ContactIdentifier(new ParticipantIdentifier("Mario")),
                        new ContactIdentifier(new ParticipantIdentifier("Luigi")));
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
