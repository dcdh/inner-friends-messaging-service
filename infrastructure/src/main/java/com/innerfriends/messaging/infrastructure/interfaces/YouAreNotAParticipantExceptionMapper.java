package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.YouAreNotAParticipantException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class YouAreNotAParticipantExceptionMapper implements ExceptionMapper<YouAreNotAParticipantException> {

    @Override
    public Response toResponse(final YouAreNotAParticipantException exception) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
