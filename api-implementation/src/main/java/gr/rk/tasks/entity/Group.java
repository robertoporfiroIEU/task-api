package gr.rk.tasks.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Entity
@Table(name = "groups")
public class Group implements GenerateCreationAt, GenerateUpdateAt {

    @Id
    private String name;

    private String description;

    private String applicationUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "groups", cascade = CascadeType.PERSIST)
    private Set<User> users;

    @OneToMany(mappedBy = "group")
    private Set<Assign> assigns;

    @OneToMany(mappedBy = "group")
    private Set<Spectator> spectators;

    private boolean deleted;

    public Group() {
        this.users = new HashSet<>();
        this.assigns = new HashSet<>();
        this.spectators = new HashSet<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setAssigns(Set<Assign> assigns) {
        assigns.forEach(a -> a.setGroup(this));
        this.assigns = assigns;
    }

    public void setSpectators(Set<Spectator> spectators) {
        spectators.forEach( s -> s.setGroup(this));
        this.spectators = spectators;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getApplicationUser() {
        return applicationUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<User> getUsers() { return users; }

    public Set<Assign> getAssigns() {
        return assigns;
    }

    public Set<Spectator> getSpectators() {
        return spectators;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @PrePersist
    @Override
    public void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    @Override
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
