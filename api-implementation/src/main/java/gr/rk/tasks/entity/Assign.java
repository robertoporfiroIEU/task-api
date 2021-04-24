package gr.rk.tasks.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assigns")
public class Assign {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID identifier;
    @ManyToOne
    private Group group;
    @ManyToOne
    private User user;
    @Column(name = "assign_date")
    private LocalDateTime assignDate;
    @ManyToOne(optional = false)
    private Task task;

    public Assign() {
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
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

    @PrePersist
    private void setAssignDate() {
        this.assignDate = LocalDateTime.now();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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

    @Override
    public String toString() {
        return "Assign{" +
                "identifier=" + identifier +
                ", group=" + group +
                ", user=" + user +
                ", assignDate=" + assignDate +
                '}';
    }
}
