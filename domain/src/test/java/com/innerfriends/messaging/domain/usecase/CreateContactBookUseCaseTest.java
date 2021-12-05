package com.innerfriends.messaging.domain.usecase;

import com.innerfriends.messaging.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateContactBookUseCaseTest {

    @Test
    public void should_create_contact_book() {
        // Given
        final ContactBookRepository contactBookRepository = mock(ContactBookRepository.class);
        final Owner owner = new Owner("Mario");
        final CreateContactBookCommand createContactBookCommand = new CreateContactBookCommand(owner,
                List.of(new ContactIdentifier("DamDamDeo")));
        final CreatedAtProvider createdAtProvider = mock(CreatedAtProvider.class);
        doReturn(buildCreatedAt()).when(createdAtProvider).now();
        final CreateContactBookUseCase createContactBookUseCase = new CreateContactBookUseCase(contactBookRepository, createdAtProvider);

        // When && Then
        assertThat(createContactBookUseCase.execute(createContactBookCommand))
                .isEqualTo(
                        new ContactBook(
                                new Owner(new ContactIdentifier("Mario")), buildCreatedAt(),
                                List.of(new Contact(new ContactIdentifier("DamDamDeo"), new AddedAt(buildCreatedAt().at())))));
        verify(contactBookRepository, times(1)).save(
                new ContactBook(
                        new Owner(new ContactIdentifier("Mario")), buildCreatedAt(),
                        List.of(new Contact(new ContactIdentifier("DamDamDeo"), new AddedAt(buildCreatedAt().at())))));
    }

    private CreatedAt buildCreatedAt() {
        return new CreatedAt(
                ZonedDateTime.of(2021, 10, 1, 0, 0, 0, 0, ZoneId.of("Europe/Paris")));
    }

}
