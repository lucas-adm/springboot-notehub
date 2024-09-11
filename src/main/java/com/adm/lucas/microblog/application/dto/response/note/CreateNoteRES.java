package com.adm.lucas.microblog.application.dto.response.note;

import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.tag.Tag;

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