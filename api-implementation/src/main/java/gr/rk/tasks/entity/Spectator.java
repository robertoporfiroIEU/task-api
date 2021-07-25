package gr.rk.tasks.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "spectators")
public class Spectator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String identifier;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "groups_name")
    private Group group;
    @ManyToOne
    @JoinColumn(name = "users_username")
    private User user;
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @ManyToOne(optional = false)
    @JoinColumn(name = "tasks_id")
    private Task task;

    private String applicationUser;

    public Spectator() {
    }

    @PrePersist
    private void setIdentifier() {
        this.identifier = UUID.randomUUID().toString();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime assignDate) {
        this.createdAt = assignDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spectator spectator = (Spectator) o;
        return Objects.equals(id, spectator.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
