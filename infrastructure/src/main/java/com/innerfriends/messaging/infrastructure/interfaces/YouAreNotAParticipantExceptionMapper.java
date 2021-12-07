package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.YouAreNotAParticipantException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class YouAreNotAParticipantExceptionMapper implements ExceptionMapper<YouAreNotAParticipantException> {

    @Override
    public Response toResponse(final YouAreNotAParticipantException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .entity("You are not in the list of participant for this conversation !")
                .build();
    }

}
