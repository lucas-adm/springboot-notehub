package xyz.xisyz.application.dto.response.comment;

import xyz.xisyz.application.dto.response.user.DetailUserRES;
import xyz.xisyz.domain.comment.Comment;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record DetailCommentRES(
        UUID id,
        String created_at,
        String text,
        boolean modified,
        DetailUserRES user,
        int replies_count
) {
    public DetailCommentRES(Comment comment) {
        this(
                comment.getId(),
                comment.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                comment.getText(),
                comment.isModified(),
                comment.getUser() != null ? new DetailUserRES(comment.getUser()) : null,
                comment.getRepliesCount()
        );
    }
}