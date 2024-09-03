package com.adm.lucas.microblog.domain.token;

import com.adm.lucas.microblog.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private String accessToken;

    private Instant createdAt = LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"));

    private Instant expiresAt;

    public Token(User user, String accessToken, Instant expiresAt) {
        this.user = user;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

}