package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ContactBookRepository;
import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.Owner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateContactBookUseCaseTest {

    @Test
    public void should_create_contact_book() {
        // Given
        final ContactBookRepository contactBookRepository = mock(ContactBookRepository.class);
        final ContactIdentifier contactIdentifier = new ContactIdentifier("Mario");
        final CreateContactBookCommand createContactBookCommand = new CreateContactBookCommand(contactIdentifier);
        final CreateContactBookUseCase createContactBookUseCase = new CreateContactBookUseCase(contactBookRepository);

        // When && Then
        assertThat(createContactBookUseCase.execute(createContactBookCommand))
                .isEqualTo(new ContactBook(
                        new Owner(
                                new ContactIdentifier("Mario"))));
        verify(contactBookRepository, times(1)).save(new ContactBook(
                new Owner(
                        new ContactIdentifier("Mario"))));
    }

}
