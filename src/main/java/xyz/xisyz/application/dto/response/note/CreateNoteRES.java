package xyz.xisyz.application.dto.response.note;

import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.tag.Tag;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public record CreateNoteRES(
        UUID id,
        String created_at,
        boolean closed,
        boolean hidden,
        String title,
        List<String> tags
) {
    public CreateNoteRES(Note note) {
        this(
                note.getId(),
                note.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                note.isClosed(),
                note.isHidden(),
                note.getTitle(),
                note.getTags().stream().map(Tag::getName).toList()
        );
    }
}