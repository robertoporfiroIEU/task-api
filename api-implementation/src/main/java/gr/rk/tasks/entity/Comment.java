package gr.rk.tasks.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID identifier;
    private String text;
    @OneToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    @Column(name="creation_date")
    private LocalDateTime creationDate;
    @ManyToOne(optional = false)
    private Task task;

    public Comment() {
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public String getText() {
        return text;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @PrePersist
    private void setCreationDate() {
        this.creationDate = LocalDateTime.now();
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
