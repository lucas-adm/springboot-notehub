package xyz.xisyz.domain.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import xyz.xisyz.domain.user.User;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"user", "fromUser"})
@ToString(exclude = {"user", "fromUser"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "from_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User fromUser;

    private Instant createdAt = Instant.now();

    private boolean read = false;

    @Convert(converter = NotificationFieldInfoConverter.class)
    private Map<String, Object> info;

    public Notification(User user, User fromUser, Map<String, Object> info) {
        this.user = user;
        this.fromUser = fromUser;
        this.info = info;
    }

    public Notification(Notification snapshot) {
        this.id = snapshot.id;
        this.user = snapshot.user;
        this.fromUser = snapshot.fromUser;
        this.createdAt = snapshot.createdAt;
        this.read = snapshot.read;
        this.info = snapshot.info;
    }

}