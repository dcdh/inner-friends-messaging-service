package com.innerfriends.messaging.infrastructure.usecase.cache;

import com.innerfriends.messaging.domain.ContactBook;
import com.innerfriends.messaging.domain.Owner;

import java.util.Optional;

public interface ContactBookCacheRepository {

    void evict(Owner owner);

    Optional<ContactBook> getByOwner(Owner owner);

    void store(ContactBook contactBook);

}
