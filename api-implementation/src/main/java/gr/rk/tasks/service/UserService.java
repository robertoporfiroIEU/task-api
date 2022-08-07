package gr.rk.tasks.service;

import gr.rk.tasks.V1.dto.UserDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Value("${applicationConfigurations.keycloak.realm}")
    private String realm;

    private Keycloak keycloak;

    @Autowired
    public UserService(KeycloakService keycloakService) {
        this.keycloak = keycloakService.getKeycloak();
    }

    public boolean isUserExist(String name) {
        List<UserDTO> users = getUsers(name);
        return users.stream().filter(user -> user.getName().equals(name)).count() > 0;
    }

    public List<UserDTO> getUsers(String name) {
        keycloak.tokenManager().getAccessToken();

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        return usersResource.search(name).stream().map(userRepresentation ->
            new UserDTO().name(userRepresentation.getUsername())
                    .email(userRepresentation.getEmail())
                    .groups(userRepresentation.getGroups())
        ).collect(Collectors.toList());
    }

}
