package com.adm.lucas.microblog.application.dto.response.note;

import com.adm.lucas.microblog.application.dto.response.user.DetailUserRES;
import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.tag.Tag;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public record LowDetailNoteRES(
        UUID id,
        String title,
        List<String> tags,
        DetailUserRES user,
        String created_at,
        boolean modified,
        boolean closed,
        int comments
) {
    public LowDetailNoteRES(Note note) {
        this(
                note.getId(),
                note.getTitle(),
                note.getTags().stream().map(Tag::getName).toList(),
                new DetailUserRES(note.getUser()),
                note.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                note.isModified(),
                note.isClosed(),
                note.getComments().size()
        );
    }
}