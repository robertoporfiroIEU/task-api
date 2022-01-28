package gr.rk.tasks.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String identifier;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true)
    private String prefixIdentifier;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Generated(GenerationTime.ALWAYS)
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String applicationUser;

    @ManyToOne()
    @JoinColumn(name = "users_username", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private Set<Task> tasks;

    private boolean deleted;

    public Project() {
        tasks = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public String getPrefixIdentifier() { return prefixIdentifier; }

    public void setPrefixIdentifier(String prefixIdentifier) { this.prefixIdentifier = prefixIdentifier; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        tasks.forEach(t -> t.setProject(this));
        this.tasks = tasks;
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
        Project project = (Project) o;
        return Objects.equals(identifier, project.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
