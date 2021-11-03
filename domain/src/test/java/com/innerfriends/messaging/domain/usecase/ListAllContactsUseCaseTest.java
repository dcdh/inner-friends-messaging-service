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
                new Contact(new ContactIdentifier(new ParticipantIdentifier("Mario")), buildAddedAt(3)),
                new Contact(new ContactIdentifier(new ParticipantIdentifier("Luigi")), buildAddedAt(2)),
                new Contact(new ContactIdentifier(new ParticipantIdentifier("Peach")), buildAddedAt(1))
        );
        final Owner owner = mock(Owner.class);
        final ContactBook contactBook = new ContactBook(owner, contacts, 1l);
        final ContactBookRepository contactBookRepository = mock(ContactBookRepository.class);
        final ListAllContactsCommand listAllContactsCommand = new ListAllContactsCommand(owner);
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);
        final ListAllContactsUseCase listAllContactsUseCase = new ListAllContactsUseCase(contactBookRepository);

        // When && Then
        assertThat(listAllContactsUseCase.execute(listAllContactsCommand))
                .containsExactly(
                        new ContactIdentifier(new ParticipantIdentifier("Luigi")),
                        new ContactIdentifier(new ParticipantIdentifier("Mario")),
                        new ContactIdentifier(new ParticipantIdentifier("Peach")));
    }

    private AddedAt buildAddedAt(final Integer day) {
        return new AddedAt(ZonedDateTime.of(2021, 10, day, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
