package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ListAllContactInContactBook;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.usecase.ListAllContactsCommand;
import com.innerfriends.messaging.domain.usecase.ListAllContactsUseCase;
import com.innerfriends.messaging.infrastructure.usecase.cache.ContactBookCacheRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedListAllContactsUseCaseTest {

    @Inject
    ManagedListAllContactsUseCase managedListAllContactsUseCase;

    @InjectMock
    ListAllContactsUseCase listAllContactsUseCase;

    @InjectMock
    ContactBookCacheRepository contactBookCacheRepository;

    @Test
    public void should_list_all_contacts_when_not_in_cache() {
        // Given
        final Owner owner = new Owner("Mario");
        doReturn(Optional.empty()).when(contactBookCacheRepository).getByOwner(owner);
        doReturn(new ListAllContactInContactBook(new ContactBook(owner)))
                .when(listAllContactsUseCase).execute(new ListAllContactsCommand(owner));
        final InOrder inOrder = inOrder(contactBookCacheRepository, listAllContactsUseCase);

        //When && Then
        assertThat(managedListAllContactsUseCase.execute(new ListAllContactsCommand(owner)))
                .isEqualTo(new ListAllContactInContactBook(new ContactBook(owner)));
        inOrder.verify(contactBookCacheRepository, times(1)).getByOwner(owner);
        inOrder.verify(listAllContactsUseCase, times(1)).execute(new ListAllContactsCommand(owner));
        inOrder.verify(contactBookCacheRepository, times(1)).store(new ContactBook(owner));
    }

    @Test
    public void should_return_from_cache_when_in_cache() {
        // Given
        final Owner owner = new Owner("Mario");
        final ContactBook contactBook = new ContactBook(owner);
        doReturn(Optional.of(contactBook)).when(contactBookCacheRepository).getByOwner(owner);

        // When && Then
        assertThat(managedListAllContactsUseCase.execute(new ListAllContactsCommand(owner)))
                .isEqualTo(new ListAllContactInContactBook(contactBook));
    }

}
