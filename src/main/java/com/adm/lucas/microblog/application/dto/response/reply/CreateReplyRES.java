package com.adm.lucas.microblog.application.dto.response.reply;

import com.adm.lucas.microblog.application.dto.response.user.DetailUserRES;
import com.adm.lucas.microblog.domain.reply.Reply;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record CreateReplyRES(
        UUID id,
        String created_at,
        String text,
        DetailUserRES user
) {
    public CreateReplyRES(Reply reply) {
        this(
                reply.getId(),
                reply.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                reply.getText(),
                new DetailUserRES(reply.getUser())
        );
    }
}