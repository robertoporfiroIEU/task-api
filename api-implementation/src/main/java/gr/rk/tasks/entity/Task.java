package gr.rk.tasks.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Entity
@Table(name = "tasks")
public class Task implements GenerateCreationAt, GenerateUpdateAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String identifier;

    @Column(nullable = false)
    private String name;

    private String description;

    private String status;

    private String label;

    private String priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String applicationUser;

    private LocalDateTime dueDate;

    @Column(name="userName")
    private String createdBy;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assign> assigns;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spectator> spectators;

    private boolean deleted;

    @ManyToOne
    @JoinColumn(name = "projects_id", nullable = false)
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

    public void setCreatedBy(String createdBy) {
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
        if (Objects.isNull(assigns) || assigns.isEmpty()) {
            this.assigns.clear();
        } else {
            this.assigns.clear();
            assigns.forEach(a -> a.setTask(this));
            this.assigns.addAll(assigns);
        }
    }

    public void setSpectators(List<Spectator> spectators) {
        if (Objects.isNull(spectators) || spectators.isEmpty()) {
            this.spectators.clear();
        } else {
            this.spectators.clear();
            spectators.forEach(s -> s.setTask(this));
            this.spectators.addAll(spectators);
        }
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

    public String getCreatedBy() {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @PrePersist
    @Override
    public void onCreate() {
        createdAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    @Override
    public void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);
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
