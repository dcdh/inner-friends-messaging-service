package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.NoContactBookFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoContactBookFoundExceptionMapper implements ExceptionMapper<NoContactBookFoundException> {

    @Override
    public Response toResponse(final NoContactBookFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
