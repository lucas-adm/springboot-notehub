package xyz.xisyz.application.dto.response.notification;

import xyz.xisyz.domain.notification.Notification;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public record DetailNotificationRES(
        UUID id,
        boolean read,
        String created_at,
        Map<String, Object> info
) {
    public DetailNotificationRES(Notification notification) {
        this(
                notification.getId(),
                notification.isRead(),
                notification.getCreatedAt()
                        .atZone(ZoneId.of("America/Sao_Paulo"))
                        .format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                notification.getInfo()
        );
    }
}