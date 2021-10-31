package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddContactIntoContactBookUseCaseTest {

    @Test
    public void should_add_contact_into_contact_book() {
        // Given
        final ContactBookRepository contactBookRepository = mock(ContactBookRepository.class);
        final AddedAtProvider addedAtProvider = mock(AddedAtProvider.class);
        final Owner owner = new Owner(new TestParticipantIdentifier("Mario"));
        final ContactBook contactBook = new ContactBook(owner);
        final ContactIdentifier contactIdentifier = new ContactIdentifier(new TestParticipantIdentifier("Peach"));
        doReturn(contactBook).when(contactBookRepository).getByOwner(owner);
        final AddContactIntoContactBookCommand addContactIntoContactBookCommand = new AddContactIntoContactBookCommand(owner, contactIdentifier);
        final AddedAt addedAt = new AddedAt(ZonedDateTime.now());
        doReturn(addedAt).when(addedAtProvider).generate();
        final AddContactIntoContactBookUseCase addContactIntoContactBookUseCase = new AddContactIntoContactBookUseCase(
                contactBookRepository, addedAtProvider);

        // When && Then
        assertThat(addContactIntoContactBookUseCase.execute(addContactIntoContactBookCommand)).isEqualTo(
                new ContactBook(owner, List.of(new Contact(contactIdentifier, addedAt))));
        verify(contactBookRepository, times(1)).save(contactBook);
    }

}
