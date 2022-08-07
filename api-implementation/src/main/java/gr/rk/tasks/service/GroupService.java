package gr.rk.tasks.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Value("${applicationConfigurations.keycloak.realm}")
    private String realm;

    private Keycloak keycloak;

    public GroupService(KeycloakService keycloakSerice) {
        this.keycloak = keycloakSerice.getKeycloak();
    }

    public boolean isGroupExist(String name) {
        List<String> groups = getGroups(name);
        return groups.stream().filter(g -> g.equals(name)).count() > 0;
    }


    public List<String> getGroups(String name) {
        keycloak.tokenManager().getAccessToken();

        RealmResource realmResource = keycloak.realm(realm);
        GroupsResource groupsResource = realmResource.groups();
        List<String> groups = groupsResource.groups().stream().map(groupRepresentation -> groupRepresentation.getName()).collect(Collectors.toList());

        if (Objects.nonNull(name)) {
            groups = groups.stream().filter(g ->g.contains(name)).collect(Collectors.toList());
        }

        return groups;
    }
}
