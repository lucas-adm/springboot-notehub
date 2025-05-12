package xyz.xisyz.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xyz.xisyz.domain.comment.Comment;
import xyz.xisyz.domain.flame.Flame;
import xyz.xisyz.domain.history.UserHistory;
import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.notification.Notification;
import xyz.xisyz.domain.reply.Reply;
import xyz.xisyz.domain.token.Token;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"token", "history", "notes", "comments", "replies", "flames", "followers", "following", "notificationsToUser", "notificationsFromUser"})
@ToString(exclude = {"token", "history", "notes", "comments", "replies", "flames", "followers", "following", "notificationsToUser", "notificationsFromUser"})
@EqualsAndHashCode(exclude = {"token", "history", "notes", "comments", "replies", "flames", "followers", "following", "notificationsToUser", "notificationsFromUser"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String providerId;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(columnDefinition = "TEXT")
    private String banner;

    private String message;

    private String password;

    private String host;

    private boolean profilePrivate = false;

    private boolean sponsor = false;

    private Long score = 0L;

    private Instant createdAt = LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"));

    private boolean active;

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private Token token;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserHistory> history = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Note> notes = new ArrayList<>();
    private int notesCount = 0;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Flame> flames = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Notification> notificationsToUser = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Notification> notificationsFromUser = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_follows",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following = new HashSet<>();
    private int followingCount = 0;

    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<User> followers = new HashSet<>();
    private int followersCount = 0;

    public User(String email, String username, String displayName, String password) {
        this.host = "XYZ";
        this.email = email;
        this.displayName = displayName;
        this.username = username;
        this.password = password;
        this.active = false;
    }

    public User(String id, String email, String username, String displayName, String avatar) {
        this.providerId = id;
        this.host = "Google";
        this.email = email;
        this.username = username.toLowerCase();
        this.displayName = displayName;
        this.avatar = avatar;
        this.active = true;
    }

    public User(Integer id, String username, String displayName, String avatar) {
        this.providerId = id.toString();
        this.host = "GitHub";
        this.username = username.toLowerCase();
        this.displayName = displayName;
        this.avatar = avatar;
        this.active = true;
    }

    public User(String username, String displayName, String avatar, String banner, String message, boolean profilePrivate) {
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
        this.banner = banner;
        this.message = message;
        this.profilePrivate = profilePrivate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_BASIC"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}