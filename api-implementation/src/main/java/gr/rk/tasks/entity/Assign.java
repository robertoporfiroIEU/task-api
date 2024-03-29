package gr.rk.tasks.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assigns")
public class Assign implements AutomaticValuesGeneration, GenerateCreationAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String identifier;

    @Column(name = "groupName")
    private String group;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tasks_id")
    private Task task;

    @Column(name = "userName")
    private String user;

    private LocalDateTime assignDate;

    private String applicationUser;

    private boolean deleted;

    @PrePersist
    @Override
    public void generateAutomatedValues() {
        if (Objects.isNull(this.identifier)) {
            this.identifier = UUID.randomUUID().toString();
        }
    }

    @Override
    public void onCreate() {
        this.assignDate = LocalDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getAssignDate() {
        return assignDate;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(String applicationUser) {
        this.applicationUser = applicationUser;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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
        Assign assign = (Assign) o;
        return Objects.equals(identifier, assign.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
