package gr.rk.tasks.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String username;

    @Email
    @Column(unique = true)
    private String email;

    private String applicationUser;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Generated(GenerationTime.ALWAYS)
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "users_has_groups",
            joinColumns = @JoinColumn(name = "users_username"),
            inverseJoinColumns = @JoinColumn(name = "groups_name"))
    private Set<Group> groups;

    @OneToMany(mappedBy = "user")
    private Set<Assign> assigns;

    @OneToMany(mappedBy = "user")
    private Set<Spectator> spectators;

    @OneToMany(mappedBy = "createdBy")
    private List<Project> projects;

    private boolean deleted;

    public User() {
        this.groups = new HashSet<>();
        this.assigns = new HashSet<>();
        this.spectators = new HashSet<>();
        this.projects = new ArrayList<>();
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

    public String getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Assign> getAssigns() {
        return assigns;
    }

    public void setAssigns(Set<Assign> assigns) {
        assigns.forEach(a -> a.setUser(this));
        this.assigns = assigns;
    }

    public Set<Spectator> getSpectators() {
        return spectators;
    }

    public void setSpectators(Set<Spectator> spectators) {
        this.spectators = spectators;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
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
}
