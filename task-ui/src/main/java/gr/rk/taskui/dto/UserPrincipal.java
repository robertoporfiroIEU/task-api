package gr.rk.taskui.dto;

import java.util.List;
import java.util.Objects;

public class UserPrincipal {
    private String name;
    private String email;
    private List<String> groups;
    private List<String> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal userPrincipal = (UserPrincipal) o;
        return Objects.equals(name, userPrincipal.name) && Objects.equals(email, userPrincipal.email) && Objects.equals(groups, userPrincipal.groups) && Objects.equals(roles, userPrincipal.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, groups, roles);
    }
}
