package gr.rk.tasks.security;

import java.util.List;

public class UserPrincipal {
    private final String applicationUser;
    private final List<String> roles;

    public UserPrincipal(String applicationUser, List<String> roles) {
        this.applicationUser = applicationUser;
        this.roles = roles;
    }


    public String getApplicationUser() {
        return applicationUser;
    }

    public List<String> getRoles() {
        return roles;
    }
}
