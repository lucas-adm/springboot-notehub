package com.adm.lucas.microblog.application.controller.dto.response.token;

import com.adm.lucas.microblog.application.controller.dto.response.user.DetailUserRES;
import com.adm.lucas.microblog.model.Token;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record AuthRES(
        UUID refreshToken,
        String accessToken,
        String createdAt,
        String expiresAt,
        DetailUserRES user
) {
    public AuthRES(Token token) {
        this(
                token.getId(),
                token.getAccessToken(),
                token.getCreatedAt().format(DateTimeFormatter.ofPattern("d/MM/yy HH:mm", Locale.of("pt-BR"))),
                token.getExpiresAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/MM/yy HH:mm", Locale.of("pt-BR"))),
                new DetailUserRES(
                        token.getUser().getId(),
                        token.getUser().getEmail(),
                        token.getUser().getUsername(),
                        token.getUser().getDisplayName(),
                        token.getUser().getAvatar(),
                        token.getUser().getHost(),
                        token.getUser().isHidden(),
                        token.getUser().isSponsor(),
                        token.getUser().getScore()
                )
        );
    }
}