package br.com.notehub.application.dto.response.reply;

import br.com.notehub.application.dto.response.user.DetailUserRES;
import br.com.notehub.domain.reply.Reply;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record DetailReplyRES(
        UUID id,
        String created_at,
        String text,
        boolean modified,
        String to_user,
        DetailUserRES user
) {
    public DetailReplyRES(Reply reply) {
        this(
                reply.getId(),
                reply.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                reply.getText(),
                reply.isModified(),
                reply.getToUser(),
                reply.getUser() != null ? new DetailUserRES(reply.getUser()) : null
        );
    }
}