package com.adm.lucas.microblog.application.dto.response.notification;

import com.adm.lucas.microblog.application.dto.response.user.DetailUserRES;
import com.adm.lucas.microblog.domain.notification.Notification;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public record DetailNotificationRES(
        UUID id,
        boolean read,
        String created_at,
        DetailUserRES from_user,
        Map<String, Object> info
) {
    public DetailNotificationRES(Notification notification) {
        this(
                notification.getId(),
                notification.isRead(),
                notification.getCreatedAt()
                        .atZone(ZoneId.of("America/Sao_Paulo"))
                        .format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                new DetailUserRES(notification.getFromUser()),
                notification.getInfo()
        );
    }
}