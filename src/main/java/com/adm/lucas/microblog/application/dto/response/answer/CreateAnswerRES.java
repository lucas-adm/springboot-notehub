package com.adm.lucas.microblog.application.dto.response.answer;

import com.adm.lucas.microblog.application.dto.response.user.DetailUserRES;
import com.adm.lucas.microblog.domain.answer.Answer;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record CreateAnswerRES(
        UUID id,
        String created_at,
        String text,
        DetailUserRES user
) {
    public CreateAnswerRES(Answer answer) {
        this(
                answer.getId(),
                answer.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                answer.getText(),
                new DetailUserRES(answer.getUser())
        );
    }
}