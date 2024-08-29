package com.adm.lucas.microblog.application.controller.dto.response.user;

import com.adm.lucas.microblog.model.User;

import java.util.UUID;

public record CreateUserRES(
        UUID id,
        String email,
        String username,
        String displayName,
        String avatar,
        String host,
        boolean hidden,
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
                user.isHidden(),
                user.isSponsor(),
                user.getScore()
        );
    }
}