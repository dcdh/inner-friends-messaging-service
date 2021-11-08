package com.innerfriends.messaging.infrastructure.usecase;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.ContactIdentifier;
import com.innerfriends.messaging.domain.Owner;
import com.innerfriends.messaging.domain.usecase.CreateContactBookCommand;
import com.innerfriends.messaging.domain.usecase.CreateContactBookUseCase;
import com.innerfriends.messaging.infrastructure.InstantProvider;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ManagedCreateContactBookUseCaseTest extends ManagedUseCaseTest {

    @Inject
    ManagedCreateContactBookUseCase managedCreateContactBookUseCase;

    @InjectMock
    CreateContactBookUseCase createContactBookUseCase;

    @InjectMock
    InstantProvider instantProvider;

    @Test
    public void should_create_contact_book() {
        // Given
        final CreateContactBookCommand createContactBookCommand = new CreateContactBookCommand(new ContactIdentifier("Mario"));
        final ContactBook contactBook = new ContactBook(new Owner("Mario"));
        doReturn(contactBook).when(createContactBookUseCase).execute(createContactBookCommand);
        doReturn(Instant.ofEpochSecond(1)).when(instantProvider).now();

        // When && Then
        assertThat(managedCreateContactBookUseCase.execute(createContactBookCommand)).isEqualTo(contactBook);
        assertThat(((Number) entityManager.createNativeQuery(COUNT_OUTBOX_EVENT_FOR_AGGREGATE_ID_SQL)
                .setParameter(1, "Mario")
                .getSingleResult()).longValue()).isEqualTo(1l);
        assertThat(entityManager.createNativeQuery(GET_TYPE_BY_AGGREGATE_ID)
                .setParameter(1, "Mario")
                .getSingleResult())
                .isEqualTo("ContactBookCreated");
    }
}
