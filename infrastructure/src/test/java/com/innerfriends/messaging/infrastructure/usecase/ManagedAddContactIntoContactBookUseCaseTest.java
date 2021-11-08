package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.AddedAt;
import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.usecase.AddContactIntoContactBookCommand;
import com.innerfriends.messaging.domain.usecase.AddContactIntoContactBookUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import com.innerfriends.messaging.infrastructure.usecase.cache.ContactBookCacheRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedAddContactIntoContactBookUseCaseTest extends ManagedUseCaseTest {

    @Inject
    ManagedAddContactIntoContactBookUseCase managedAddContactIntoContactBookUseCase;

    @InjectMock
    AddContactIntoContactBookUseCase addContactIntoContactBookUseCase;

    @InjectMock
    ContactBookCacheRepository contactBookCacheRepository;

    @InjectMock
    InstantProvider instantProvider;

    @Test
    public void should_add_contact_into_contact_book() {
        // Given
        final Owner owner = new Owner("Mario");
        final ContactIdentifier contactIdentifier = new ContactIdentifier("Peach");
        final AddContactIntoContactBookCommand addContactIntoContactBookCommand = new AddContactIntoContactBookCommand(owner, contactIdentifier);
        final InOrder inOrder = inOrder(contactBookCacheRepository, addContactIntoContactBookUseCase, instantProvider);
        final ContactBook contactBook = new ContactBook(owner);
        contactBook.addNewContact(new ContactIdentifier("Pach"), new AddedAt(ZonedDateTime.now()));
        doReturn(contactBook).when(addContactIntoContactBookUseCase).execute(addContactIntoContactBookCommand);
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When && Then
        assertThat(managedAddContactIntoContactBookUseCase.execute(addContactIntoContactBookCommand)).isEqualTo(contactBook);

        inOrder.verify(contactBookCacheRepository, times(1)).evict(owner);
        inOrder.verify(addContactIntoContactBookUseCase, times(1)).execute(addContactIntoContactBookCommand);
        assertThat(((Number) entityManager.createNativeQuery(COUNT_OUTBOX_EVENT_FOR_AGGREGATE_ID_SQL)
                .setParameter(1, "Mario")
                .getSingleResult()).longValue()).isEqualTo(1l);
        assertThat(entityManager.createNativeQuery(GET_TYPE_BY_AGGREGATE_ID)
                .setParameter(1, "Mario")
                .getSingleResult())
                .isEqualTo("ContactAddedIntoContactBook");
    }

}
