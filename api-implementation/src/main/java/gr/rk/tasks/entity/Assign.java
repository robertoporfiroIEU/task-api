package gr.rk.tasks.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assigns")
public class Assign implements AutomaticValuesGeneration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String identifier;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "groups_name")
    private Group group;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tasks_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "users_username")
    private User user;

    @Generated(GenerationTime.INSERT)
    @Column(insertable = false, updatable = false)
    private LocalDateTime assignDate;

    private String applicationUser;

    public Assign() {}

    @PrePersist
    @Override
    public void generateAutomatedValues() {
        if (Objects.isNull(this.identifier)) {
            this.identifier = UUID.randomUUID().toString();
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(LocalDateTime assignDate) {
        this.assignDate = assignDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assign assign = (Assign) o;
        return Objects.equals(id, assign.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
