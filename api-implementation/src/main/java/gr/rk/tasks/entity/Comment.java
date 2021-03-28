package gr.rk.tasks.entity;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
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

    @CreationTimestamp
    @Column(name="creation_date")
    private Timestamp creationDate;

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

    public Timestamp getCreationDate() {
        return creationDate;
    }
}
