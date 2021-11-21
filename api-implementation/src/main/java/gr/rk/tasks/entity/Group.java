package gr.rk.tasks.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    private String name;

    private String description;

    private String applicationUser;

    @Generated(GenerationTime.INSERT)
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Generated(GenerationTime.ALWAYS)
    @Column(insertable = false, updatable = false)
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

    public void setCreatedAt(LocalDateTime createAt) {
        this.createdAt = createAt;
    }

    public void setUpdatedAt(LocalDateTime updateAt) {
        this.updatedAt = updateAt;
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
