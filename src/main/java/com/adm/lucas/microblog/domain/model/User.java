package com.adm.lucas.microblog.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
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

    private boolean hidden = false;

    private boolean sponsor = false;

    private Long score = 0L;

    private boolean active = true;

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private Token token;

    public User(String email, String username, String displayName, String avatar, String password) {
        this.host = "Microblog";
        this.email = email;
        this.displayName = displayName;
        this.username = username;
        this.avatar = avatar;
        this.password = password;
    }

    public User(String email, String username, String displayName, String avatar) {
        this.host = "Google";
        this.email = email;
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
    }

    public User(String username, String displayName, String avatar) {
        this.host = "GitHub";
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
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