package gr.rk.tasks.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    private String name;
    private String description;
    private String applicationUser;
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL)
    private List<User> users;
    @OneToMany(mappedBy = "group")
    private List<Assign> assigns;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Spectator> spectators;

    public Group() {
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

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setAssigns(List<Assign> assigns) {
        this.assigns = assigns;
    }

    public void setSpectators(List<Spectator> spectators) {
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

    public List<User> getUsers() {
        return users;
    }

    public List<Assign> getAssigns() {
        return assigns;
    }

    public List<Spectator> getSpectators() {
        return spectators;
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
