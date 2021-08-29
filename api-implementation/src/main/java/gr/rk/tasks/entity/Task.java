package gr.rk.tasks.entity;

import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task implements AutomaticValuesGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String identifier;

    @Column(nullable = false)
    private String name;

    private String description;

    private String status;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String applicationUser;

    private LocalDateTime dueDate;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "users_username")
    private User createdBy;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Assign> assigns;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Spectator> spectators;

    @Transient
    private UserPrincipal userPrincipal;

    public Task() {
        this.comments = new ArrayList<>();
        this.assigns = new ArrayList<>();
        this.spectators = new ArrayList<>();
    }

    @Autowired
    public Task(UserPrincipal userPrincipal) {
        this();
        this.userPrincipal = userPrincipal;
    }

    @PrePersist
    @Override
    public void generateAutomatedValues() {
        if (Objects.isNull(this.identifier)) {
            this.identifier = UUID.randomUUID().toString();
        }
        if (Objects.isNull(this.applicationUser)) {
            this.applicationUser = this.userPrincipal.getApplicationUser();
        }
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setAssigns(List<Assign> assigns) {
        this.assigns = assigns;
    }

    public void setSpectators(List<Spectator> spectators) {
        this.spectators = spectators;
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getApplicationUser() {
        return applicationUser;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public List<Comment> getComments() {
        return comments;
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
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
