package gr.rk.tasks.service;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class KeycloakService {

    @Value("${applicationConfigurations.keycloak.authServerUrl}")
    private String authServerUrl;
    @Value("${applicationConfigurations.keycloak.realm}")
    private String realm;
    @Value("${applicationConfigurations.keycloak.clientId}")
    private String clientId;
    @Value("${applicationConfigurations.keycloak.clientSecret}")
    private String clientSecret;
    private Keycloak keycloak;

    public KeycloakService() {}

    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
    }

    public Keycloak getKeycloak() {
        return keycloak;
    }

}
