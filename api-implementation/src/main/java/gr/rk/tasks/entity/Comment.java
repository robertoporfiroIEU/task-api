package gr.rk.tasks.entity;

import gr.rk.tasks.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class Comment implements AutomaticValuesGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "users_username")
    private User createdBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tasks_id")
    private Task task;

    @Column(unique = true)
    private String identifier;

    private String text;

    private String applicationUser;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Transient
    private UserPrincipal userPrincipal;

    public Comment() {}

    @Autowired
    public Comment(UserPrincipal userPrincipal) {
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

    public String getIdentifier() {
        return identifier;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
