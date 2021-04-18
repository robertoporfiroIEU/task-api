package gr.rk.tasks.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String username;
    @Email
    @Column(unique = true)
    private String email;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_groups",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "group_name"))
    private List<Group> groups;
    @OneToMany(mappedBy = "user")
    private List<Assign> assigns;
    @OneToMany(mappedBy = "user")
    private List<Spectator> spectators;

    public User() {
        this.groups = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Assign> getAssigns() {
        return assigns;
    }

    public void setAssigns(List<Assign> assigns) {
        this.assigns = assigns;
    }

    public List<Spectator> getSpectators() {
        return spectators;
    }

    public void setSpectators(List<Spectator> spectators) {
        this.spectators = spectators;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
