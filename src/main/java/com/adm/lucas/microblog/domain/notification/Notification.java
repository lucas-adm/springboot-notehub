package com.adm.lucas.microblog.domain.notification;

import com.adm.lucas.microblog.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @ManyToOne
    private User user;

    @JoinColumn(name = "from_user_id")
    @ManyToOne
    private User fromUser;

    private Instant createdAt = Instant.now();

    private boolean read = false;

    //    @Column(columnDefinition = "JSON") // PostgreSQL
    @Convert(converter = NotificationFieldInfoConverter.class) // H2
    private Map<String, Object> info;

    public Notification(User user, User fromUser, Map<String, Object> info) {
        this.user = user;
        this.fromUser = fromUser;
        this.info = info;
    }

}