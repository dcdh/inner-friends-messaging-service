package com.innerfriends.messaging.infrastructure;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class KeycloakClient {

    private static final String PASSWORD_GRANT_TYPE = "password";

    private final RemoteKeycloakService remoteKeycloakService;
    private final String clientId;
    private final String clientSecret;
    private final String userRealm;
    private final String keycloakAdminRealm;
    private final String keycloakAdminClientId;
    private final String keycloakAdminUsername;
    private final String keycloakAdminPassword;

    public KeycloakClient(@ConfigProperty(name = "quarkus.oidc.auth-server-url") final String authServerUrl,
                          @ConfigProperty(name = "quarkus.oidc.client-id") final String clientId,
                          @ConfigProperty(name = "quarkus.oidc.credentials.secret") final String clientSecret,
                          @ConfigProperty(name = "keycloak.admin.realm") final String keycloakAdminRealm,
                          @ConfigProperty(name = "keycloak.admin.clientId") final String keycloakAdminClientId,
                          @ConfigProperty(name = "keycloak.admin.username") final String keycloakAdminUsername,
                          @ConfigProperty(name = "keycloak.admin.password") final String adminPassword,
                          @RestClient final RemoteKeycloakService remoteKeycloakService) {
        final Pattern pattern = Pattern.compile("^(.*)\\/realms\\/(.*)$");
        final Matcher matcher = pattern.matcher(authServerUrl);
        if (!matcher.matches()) {
            throw new IllegalStateException("bad conf");
        }
        this.userRealm = matcher.group(2);
        if (!this.userRealm.equals("public")) {
            throw new IllegalStateException("Invalid realm - expecting public");
        }
        this.clientId = Objects.requireNonNull(clientId);
        this.clientSecret = Objects.requireNonNull(clientSecret);
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
            remoteKeycloakService.create("bearer " + accessTokenResponse.accessToken, userRealm, userRepresentation);
        } catch (final WebApplicationException webApplicationException) {
            System.out.println(webApplicationException.getResponse());// I obtain a 409 because Mario is already present. Expected.
        }
    }

    public RemoteKeycloakService.AccessTokenResponse grantTokenFromPublicRealm(final String username, final String password) {
        return remoteKeycloakService.grantTokenFromConfidentialAccessType(userRealm, PASSWORD_GRANT_TYPE, clientId, clientSecret, username, password);
    }

}
