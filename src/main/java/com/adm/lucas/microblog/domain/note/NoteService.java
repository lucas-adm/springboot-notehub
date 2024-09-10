package com.adm.lucas.microblog.domain.note;

import com.adm.lucas.microblog.application.dto.request.note.CreateNoteREQ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface NoteService {

    Note mapToNote(UUID idFromToken, CreateNoteREQ req);

    Note create(Note note);

    void changeTags(UUID idFromToken, UUID idFromPath, List<String> tags);

    void delete(UUID idFromToken, UUID idFromPath);

}