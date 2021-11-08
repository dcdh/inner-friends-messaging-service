package com.innerfriends.messaging.infrastructure.interfaces;

import com.innerfriends.messaging.domain.UnknownConversationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnknownConversationExceptionMapper implements ExceptionMapper<UnknownConversationException> {

    @Override
    public Response toResponse(final UnknownConversationException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
