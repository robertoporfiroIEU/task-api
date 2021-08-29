package gr.rk.tasks.entity;

import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "groups")
public class Group implements AutomaticValuesGeneration {

    @Id
    private String name;

    private String description;

    private String applicationUser;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "groups", cascade = CascadeType.PERSIST)
    private List<User> users;

    @OneToMany(mappedBy = "group")
    private List<Assign> assigns;

    @OneToMany(mappedBy = "group")
    private List<Spectator> spectators;

    @Transient
    private UserPrincipal userPrincipal;

    public Group() {
        this.users = new ArrayList<>();
        this.assigns = new ArrayList<>();
        this.spectators = new ArrayList<>();
    }

    @Autowired
    public Group(UserPrincipal userPrincipal) {
        this();
        this.userPrincipal = userPrincipal;
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

    @PrePersist
    @Override
    public void generateAutomatedValues() {
        if (Objects.isNull(this.applicationUser)) {
            this.applicationUser = this.userPrincipal.getApplicationUser();
        }
    }
}
