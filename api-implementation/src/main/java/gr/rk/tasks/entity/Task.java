package gr.rk.tasks.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String status;

    @Generated(GenerationTime.INSERT)
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Generated(GenerationTime.ALWAYS)
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String applicationUser;

    private LocalDateTime dueDate;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "users_username", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Assign> assigns;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Spectator> spectators;

    @ManyToOne()
    @JoinColumn(name = "projects_id")
    private Project project;

    public Task() {
        this.comments = new ArrayList<>();
        this.assigns = new ArrayList<>();
        this.spectators = new ArrayList<>();
    }

    public void setId(Long id) { this.id = id; }

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

    public void setProject(Project project) {
        this.project = project;
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

    public Project getProject() { return project; }

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
