package br.com.notehub.application.dto.response.flame;

import br.com.notehub.application.dto.response.note.LowDetailNoteRES;
import br.com.notehub.domain.flame.Flame;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record DetailFlameRES(
        String created_at,
        LowDetailNoteRES note
) {
    public DetailFlameRES(Flame flame) {
        this(
                flame.getCreatedAt().atZone(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("d/M/yy HH:mm", Locale.of("pt-BR"))),
                new LowDetailNoteRES(flame.getNote())
        );
    }
}