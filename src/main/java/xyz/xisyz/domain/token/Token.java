package xyz.xisyz.domain.token;

import xyz.xisyz.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"user"})
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
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