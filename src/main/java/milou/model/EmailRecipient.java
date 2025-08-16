package milou.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_recipients")
public class EmailRecipient {
    @EmbeddedId
    private EmailRecipientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("emailId")
    @JoinColumn(name = "email_id")
    private Email email;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public EmailRecipient() {}

    public EmailRecipient(Email email, User user) {
        this.email = email; this.user = user;
        this.id = new EmailRecipientId(email.getId(), user.getId());
        this.read = false;
    }

    public EmailRecipientId getId() { return id; }
    public Email getEmail() { return email; }
    public User getUser() { return user; }
    public boolean isRead() { return read; }
    public LocalDateTime getReadAt() { return readAt; }

    public void markRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }
}
