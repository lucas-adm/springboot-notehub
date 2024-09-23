package com.adm.lucas.microblog.application.dto.response.user;

import com.adm.lucas.microblog.domain.user.User;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record DetailUserRES(
        String created_at,
        String username,
        String display_name,
        String avatar,
        String banner,
        String message,
        boolean sponsor,
        int followers,
        int following
) {
    public DetailUserRES(User user) {
        this(
                user.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy", Locale.of("pt-BR"))),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatar(),
                user.getBanner(),
                user.getMessage(),
                user.isSponsor(),
                user.getFollowers().size(),
                user.getFollowing().size()
        );
    }
}