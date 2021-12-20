package com.innerfriends.messaging.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@RegisterRestClient(configKey="keycloak-remote-service")
public interface RemoteKeycloakService {

    /**
     * "Public" access used from web client. A secret could not be safe from a client side point of view.
     */
    @POST
    @Path("/auth/realms/{realm}/protocol/openid-connect/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    AccessTokenResponse grantTokenFromPublicAccessType(@PathParam("realm") String realm,
                                                       @FormParam("grant_type") String grantType,
                                                       @FormParam("client_id") String clientId,
                                                       @FormParam("username") String username,
                                                       @FormParam("password") String password);

    /**
     * "Confidential" access used when doing request only in server side.
     */
    @POST
    @Path("/auth/realms/{realm}/protocol/openid-connect/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    AccessTokenResponse grantTokenFromConfidentialAccessType(@PathParam("realm") String realm,
                                                             @FormParam("grant_type") String grantType,
                                                             @FormParam("client_id") String clientId,
                                                             @FormParam("client_secret") String clientSecret,
                                                             @FormParam("username") String username,
                                                             @FormParam("password") String password);

    class AccessTokenResponse {
        @JsonProperty("access_token")
        public String accessToken;
    }

    @POST
    @Path("/auth/admin/realms/{realm}/users")
    @Consumes(MediaType.APPLICATION_JSON)
    Response create(@HeaderParam("Authorization") String bearer,
                    @PathParam("realm") String realm,
                    UserRepresentation userRepresentation);

    class UserRepresentation {
        public final String username;
        public final String email;
        public final List<CredentialRepresentation> credentials;
        public final Boolean enabled;
        public final Map<String, List<String>> attributes;
        public final List<String> groups;

        public UserRepresentation(final String username) {
            this.username = username;
            this.email = username + "@inner-friends.com";
            this.credentials = List.of(new CredentialRepresentation());
            this.enabled = true;
            this.attributes = Map.of("friendId", List.of(username));
            this.groups = List.of("public_friend_roles");
        }
    }

    class CredentialRepresentation {
        public final String type = "password";
        public final String value = "password";
        public final String temporary = "false";
    }

}
