package xyz.xisyz.domain.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import xyz.xisyz.domain.user.User;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"user"})
@ToString(exclude = {"user"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Instant createdAt = Instant.now();

    private boolean read = false;

    @Column(columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = NotificationFieldInfoConverter.class)
    private Map<String, Object> info;

    public Notification(User user, Map<String, Object> info) {
        this.user = user;
        this.info = info;
    }

    public Notification(Notification snapshot) {
        this.id = snapshot.id;
        this.user = snapshot.user;
        this.createdAt = snapshot.createdAt;
        this.read = snapshot.read;
        this.info = snapshot.info;
    }

}