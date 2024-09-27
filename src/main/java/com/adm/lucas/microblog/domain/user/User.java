package com.adm.lucas.microblog.domain.user;

import com.adm.lucas.microblog.domain.flame.Flame;
import com.adm.lucas.microblog.domain.notification.Notification;
import com.adm.lucas.microblog.domain.reply.Reply;
import com.adm.lucas.microblog.domain.comment.Comment;
import com.adm.lucas.microblog.domain.history.UserHistory;
import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@EqualsAndHashCode(exclude = {"token", "followers", "following"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @OneToMany(mappedBy = "user")
    private List<UserHistory> history = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Flame> flames = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Notification> notificationsToUser = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser", orphanRemoval = true)
    private List<Notification> notificationsFromUser = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_followers", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<User> followers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_following", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<User> following = new HashSet<>();

    public User(String email, String username, String displayName, String avatar, String password) {
        this.host = "Microblog";
        this.email = email;
        this.displayName = displayName;
        this.username = username;
        this.avatar = avatar;
        this.password = password;
        this.active = true;
    }

    public User(String email, String username, String displayName, String avatar) {
        this.host = "Google";
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
        this.active = false;
    }

    public User(String username, String displayName, String avatar) {
        this.host = "GitHub";
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
        this.active = true;
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