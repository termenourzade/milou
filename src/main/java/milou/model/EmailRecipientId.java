package milou.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EmailRecipientId implements Serializable {
    private Integer emailId;
    private Integer userId;

    public EmailRecipientId() {}
    public EmailRecipientId(Integer emailId, Integer userId) {
        this.emailId = emailId; this.userId = userId;
    }

    public Integer getEmailId() { return emailId; }
    public Integer getUserId() { return userId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailRecipientId that)) return false;
        return Objects.equals(emailId, that.emailId) && Objects.equals(userId, that.userId);
    }
    @Override public int hashCode() {
        return Objects.hash(emailId, userId); }
}


