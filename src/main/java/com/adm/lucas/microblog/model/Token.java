package com.adm.lucas.microblog.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
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
    @OneToOne
    private User user;

    private String accessToken;

    private LocalDateTime createdAt = LocalDateTime.now();

    private Instant expiresAt;

    public Token(User user, String accessToken, Instant expiresAt) {
        this.user = user;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

}