package gr.rk.tasks.security;

import java.util.List;

public class UserPrincipal {
    private final String applicationUser;
    private final String propagatedUser;
    private final List<String> roles;

    public UserPrincipal(String applicationUser,  String propagatedUser, List<String> roles) {
        this.applicationUser = applicationUser;
        this.propagatedUser = propagatedUser;
        this.roles = roles;
    }

    public String getApplicationUser() {
        return applicationUser;
    }

    public String getPropagatedUser() {
        return propagatedUser;
    }

    public List<String> getRoles() {
        return roles;
    }
}
