package xyz.xisyz.application.dto.response.note;

import xyz.xisyz.application.dto.response.user.DetailUserRES;
import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.tag.Tag;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public record DetailNoteRES(
        UUID id,
        String title,
        List<String> tags,
        DetailUserRES user,
        String created_at,
        String modified_at,
        boolean modified,
        boolean closed,
        boolean hidden,
        String markdown,
        int comments_count,
        int flames_count
) {
    public DetailNoteRES(Note note) {
        this(
                note.getId(),
                note.getTitle(),
                note.getTags().stream().map(Tag::getName).toList(),
                note.getUser() != null ? new DetailUserRES(note.getUser()) : null,
                note.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                note.getModifiedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                note.isModified(),
                note.isClosed(),
                note.isHidden(),
                note.getMarkdown(),
                note.getCommentsCount(),
                note.getFlamesCount()
        );
    }
}