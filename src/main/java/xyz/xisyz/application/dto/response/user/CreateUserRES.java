package xyz.xisyz.application.dto.response.user;

import xyz.xisyz.domain.user.User;

import java.util.UUID;

public record CreateUserRES(
        UUID id,
        String email,
        String username,
        String display_name,
        String avatar,
        String host,
        boolean profile_private,
        boolean sponsor,
        Long score
) {
    public CreateUserRES(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatar(),
                user.getHost(),
                user.isProfilePrivate(),
                user.isSponsor(),
                user.getScore()
        );
    }
}