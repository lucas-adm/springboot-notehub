package com.adm.lucas.microblog.domain.history;

import com.adm.lucas.microblog.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "users_history")
@NoArgsConstructor
@Data
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    private String username;

    private Instant dateTime = LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"));

    private String field;

    private String oldValue;

    private String newValue;

    public UserHistory(User user, String field, String oldValue, String newValue) {
        this.user = user;
        this.username = user.getUsername();
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}