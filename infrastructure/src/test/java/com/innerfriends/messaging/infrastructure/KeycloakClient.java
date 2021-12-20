package com.innerfriends.messaging.infrastructure;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.util.Objects;

@ApplicationScoped
public class KeycloakClient {

    private static final String PASSWORD_GRANT_TYPE = "password";

    private final RemoteKeycloakService remoteKeycloakService;
    private final String keycloakPublicRealm;
    private final String keycloakPublicClientId;
    private final String keycloakPublicCredentialsSecret;
    private final String keycloakAdminRealm;
    private final String keycloakAdminClientId;
    private final String keycloakAdminUsername;
    private final String keycloakAdminPassword;

    public KeycloakClient(@ConfigProperty(name = "keycloak.public.realm") final String keycloakPublicRealm,
                          @ConfigProperty(name = "keycloak.public.client-id") final String keycloakPublicClientId,
                          @ConfigProperty(name = "keycloak.public.credentials.secret") final String keycloakPublicCredentialsSecret,
                          @ConfigProperty(name = "keycloak.admin.realm") final String keycloakAdminRealm,
                          @ConfigProperty(name = "keycloak.admin.clientId") final String keycloakAdminClientId,
                          @ConfigProperty(name = "keycloak.admin.username") final String keycloakAdminUsername,
                          @ConfigProperty(name = "keycloak.admin.password") final String adminPassword,
                          @RestClient final RemoteKeycloakService remoteKeycloakService) {
        this.keycloakPublicRealm = Objects.requireNonNull(keycloakPublicRealm);
        this.keycloakPublicClientId = Objects.requireNonNull(keycloakPublicClientId);
        this.keycloakPublicCredentialsSecret = Objects.requireNonNull(keycloakPublicCredentialsSecret);
        this.keycloakAdminRealm = Objects.requireNonNull(keycloakAdminRealm);
        this.keycloakAdminClientId = Objects.requireNonNull(keycloakAdminClientId);
        this.keycloakAdminUsername = Objects.requireNonNull(keycloakAdminUsername);
        this.keycloakAdminPassword = Objects.requireNonNull(adminPassword);
        this.remoteKeycloakService = Objects.requireNonNull(remoteKeycloakService);
    }

    public void registerUserIntoPublicRealm(final String owner) {
        final RemoteKeycloakService.UserRepresentation userRepresentation = new RemoteKeycloakService.UserRepresentation(owner);
        final RemoteKeycloakService.AccessTokenResponse accessTokenResponse = remoteKeycloakService.grantTokenFromPublicAccessType(keycloakAdminRealm,
                PASSWORD_GRANT_TYPE, keycloakAdminClientId, keycloakAdminUsername, keycloakAdminPassword);
        try {
            remoteKeycloakService.create("bearer " + accessTokenResponse.accessToken, keycloakPublicRealm, userRepresentation);
        } catch (final WebApplicationException webApplicationException) {
            System.out.println(webApplicationException.getResponse());// I obtain a 409 because Mario is already present. Expected.
        }
    }

    public RemoteKeycloakService.AccessTokenResponse grantTokenFromPublicRealm(final String username) {
        return remoteKeycloakService.grantTokenFromConfidentialAccessType(keycloakPublicRealm,
                PASSWORD_GRANT_TYPE, keycloakPublicClientId, keycloakPublicCredentialsSecret, username, "password");
    }

}
