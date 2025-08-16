package milou.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 6, nullable = false, unique = true)
    private String code;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmailRecipient> recipients = new HashSet<>();


    public Email() {}

    public Email(String code, User sender, String subject, String body) {
        this.code = code;
        this.sender = sender;
        this.subject = subject;
        this.body = body;
    }

    public Integer getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public LocalDateTime getSentAt() { return sentAt; }
    public Set<EmailRecipient> getRecipients() { return recipients; }
}
