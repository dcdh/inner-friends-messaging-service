package com.innerfriends.messaging.infrastructure.postgres;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import java.util.concurrent.Callable;

public abstract class RepositoryTesting {

    public static final String DELETE_ALL_IN_T_CONSUMED_MESSAGE = "DELETE FROM public.T_CONSUMED_MESSAGE";
    public static final String DELETE_ALL_IN_T_CONTACT_BOOK = "DELETE FROM public.T_CONTACT_BOOK";
    public static final String DELETE_ALL_IN_T_CONVERSATION = "DELETE FROM public.T_CONVERSATION";

    @Inject
    UserTransaction userTransaction;

    @Inject
    EntityManager entityManager;

    @BeforeEach
    @AfterEach
    public void flush() throws Exception {
        runInTransaction(() ->
                entityManager.createNativeQuery(DELETE_ALL_IN_T_CONSUMED_MESSAGE).executeUpdate());
        runInTransaction(() ->
                entityManager.createNativeQuery(DELETE_ALL_IN_T_CONTACT_BOOK).executeUpdate());
        runInTransaction(() ->
                entityManager.createNativeQuery(DELETE_ALL_IN_T_CONVERSATION).executeUpdate());
    }

    public <V> V runInTransaction(final Callable<V> callable) throws Exception {
        userTransaction.begin();
        try {
            return callable.call();
        } catch (final Exception exception) {
            throw exception;
        } finally {
            userTransaction.commit();
        }
    }

}
