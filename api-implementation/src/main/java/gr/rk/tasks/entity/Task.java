package gr.rk.tasks.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String identifier;

    @Column(nullable = false)
    private String name;

    private String description;

    private String status;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Generated(GenerationTime.ALWAYS)
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String applicationUser;

    private LocalDateTime dueDate;

    @OneToOne
    @JoinColumn(name = "users_username", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Assign> assigns;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Spectator> spectators;

    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "projects_id")
    private Project project;

    public Task() {
        this.comments = new HashSet<>();
        this.assigns = new ArrayList<>();
        this.spectators = new ArrayList<>();
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

    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setComments(Set<Comment> comments) {
        comments.forEach( c -> c.setTask(this));
        this.comments = comments;
    }

    public void setAssigns(List<Assign> assigns) {
        if (Objects.nonNull(assigns)) {
            assigns.forEach(a -> a.setTask(this));
        }
        this.assigns = assigns;
    }

    public void setSpectators(List<Spectator> spectators) {
        if (Objects.nonNull(spectators)) {
            spectators.forEach(s -> s.setTask(this));
        }
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

    public Project getProject() {
        return project;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public List<Assign> getAssigns() {
        return assigns;
    }

    public List<Spectator> getSpectators() {
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
        Task task = (Task) o;
        return Objects.equals(identifier, task.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
