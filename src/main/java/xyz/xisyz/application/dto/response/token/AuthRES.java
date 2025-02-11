package xyz.xisyz.application.dto.response.token;

import xyz.xisyz.application.dto.response.user.PersonalUserRES;
import xyz.xisyz.domain.token.Token;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

record Payload(
        UUID refresh_token,
        String access_token,
        String created_at,
        String expires_at
) {
}

public record AuthRES(
        Payload token,
        PersonalUserRES user
) {
    public AuthRES(Token token) {
        this(
                new Payload(
                        token.getId(),
                        token.getAccessToken(),
                        token.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                        token.getExpiresAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR")))
                ),
                new PersonalUserRES(
                        token.getUser().getId(),
                        token.getUser().getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                        token.getUser().getEmail(),
                        token.getUser().getUsername(),
                        token.getUser().getDisplayName(),
                        token.getUser().getAvatar(),
                        token.getUser().getBanner(),
                        token.getUser().getMessage(),
                        token.getUser().getHost(),
                        token.getUser().isProfilePrivate(),
                        token.getUser().isSponsor(),
                        token.getUser().getScore(),
                        token.getUser().getFollowersCount(),
                        token.getUser().getFollowingCount(),
                        token.getUser().getNotificationsToUser().stream().filter(notification -> !notification.isRead()).count()
                )
        );
    }
}