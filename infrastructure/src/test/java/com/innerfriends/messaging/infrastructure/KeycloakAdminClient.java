package com.innerfriends.messaging.infrastructure;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class KeycloakAdminClient {

    private final Keycloak keycloak;
    private final String userRealm;

    public KeycloakAdminClient(@ConfigProperty(name = "quarkus.oidc.auth-server-url") final String authServerUrl,
                               @ConfigProperty(name = "keycloak.admin.adminRealm") final String keycloakAdminAdminRealm,
                               @ConfigProperty(name = "keycloak.admin.clientId") final String keycloakAdminClientId,
                               @ConfigProperty(name = "keycloak.admin.username") final String keycloakAdminUsername,
                               @ConfigProperty(name = "keycloak.admin.password") final String keycloakAdminPassword) {
        final Pattern pattern = Pattern.compile("^(.*)\\/realms\\/(.*)$");
        final Matcher matcher = pattern.matcher(authServerUrl);
        if (!matcher.matches()) {
            throw new IllegalStateException("bad conf");
        }
        final String serverUrl = matcher.group(1);
        this.keycloak = Keycloak.getInstance(serverUrl, keycloakAdminAdminRealm, keycloakAdminUsername, keycloakAdminPassword, keycloakAdminClientId);
        this.userRealm = matcher.group(2);
        if (!this.userRealm.equals("public")) {
            throw new IllegalStateException("Invalid realm - expecting public");
        }
    }

    public void register(final String friendId) {
        final CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(friendId);

        final UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(friendId);// Will be stored in lowercase.
        keycloakUser.setEmail(friendId + "@innerfriends.com");
        keycloakUser.setCredentials(List.of(credential));
        keycloakUser.setEnabled(true);
        keycloakUser.setAttributes(Map.of("friendId", List.of(friendId)));

        // Get realm
        final RealmResource realmResource = keycloak.realm(userRealm);
        final UsersResource usersRessource = realmResource.users();

        // Create Keycloak user
        final Response response = usersRessource.create(keycloakUser);
        if (response.getStatus() != 201) {
            throw new IllegalStateException("Unable to register friend into keycloak");
        }
    }

}
