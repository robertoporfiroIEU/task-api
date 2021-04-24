package gr.rk.tasks.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    private UUID identifier;
    private String name;
    private String description;
    private String status;
    private LocalDateTime creationDate;
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private User createdBy;
    private String realm;
    private LocalDateTime dueDate;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Assign> assigns;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Spectator> spectators;

    public Task() {
        this.comments = new ArrayList<>();
        this.assigns = new ArrayList<>();
        this.spectators = new ArrayList<>();
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @PrePersist
    private void setCreationDate() {
        this.creationDate = LocalDateTime.now();
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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
        Task task = (Task) o;
        return Objects.equals(identifier, task.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
