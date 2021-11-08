package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.ParticipantsAreNotInContactBookException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ParticipantsAreNotInContactBookExceptionMapper implements ExceptionMapper<ParticipantsAreNotInContactBookException> {

    @Override
    public Response toResponse(final ParticipantsAreNotInContactBookException exception) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

}
