package com.innerfriends.messaging.domain;

public interface ContactBookRepository {

    ContactBook getByOwner(Owner owner) throws NoContactBookFoundException;

    void save(ContactBook contactBook) throws NoContactBookFoundException;

}
