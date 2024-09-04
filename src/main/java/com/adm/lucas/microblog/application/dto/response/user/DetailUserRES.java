package com.adm.lucas.microblog.application.dto.response.user;

import com.adm.lucas.microblog.domain.user.User;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record DetailUserRES(
        UUID id,
        String created_at,
        String email,
        String username,
        String display_name,
        String avatar,
        String host,
        boolean profile_private,
        boolean sponsor,
        Long score
) {
    public DetailUserRES(User user) {
        this(
                user.getId(),
                user.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/MM/yy", Locale.of("pt-BR"))),
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