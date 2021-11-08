package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.usecase.ListRecentContactsCommand;
import com.innerfriends.messaging.domain.usecase.ListRecentContactsUseCase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedListRecentContactsUseCaseTest {

    @Inject
    ManagedListRecentContactsUseCase managedListRecentContactsUseCase;

    @InjectMock
    ListRecentContactsUseCase listRecentContactsUseCase;

    @Test
    public void should_list_recent_contacts() {
        // Given
        final ListRecentContactsCommand listRecentContactsCommand = new ListRecentContactsCommand(new Owner("Mario"), 2);
        final List<ContactIdentifier> recentContacts = List.of(mock(ContactIdentifier.class));
        doReturn(recentContacts).when(listRecentContactsUseCase).execute(listRecentContactsCommand);

        // When && Then
        assertThat(managedListRecentContactsUseCase.execute(listRecentContactsCommand))
                .isEqualTo(recentContacts);
    }
}
