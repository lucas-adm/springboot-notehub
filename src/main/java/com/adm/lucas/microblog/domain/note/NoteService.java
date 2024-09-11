package com.adm.lucas.microblog.domain.note;

import com.adm.lucas.microblog.application.dto.request.note.CreateNoteREQ;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface NoteService {

    Note mapToNote(UUID idFromToken, CreateNoteREQ req);

    Note create(Note note);

    void edit(UUID idFromToken, UUID idFromPath, String title, List<String> tags, boolean closed, boolean hidden);

    void changeTitle(UUID idFromToken, UUID idFromPath, String title);

    void changeMarkdown(UUID idFromToken, UUID idFromPath, String markdown);

    void changeClosed(UUID idFromToken, UUID idFromPath);

    void changeHidden(UUID idFromToken, UUID idFromPath);

    void changeTags(UUID idFromToken, UUID idFromPath, List<String> tags);

    void delete(UUID idFromToken, UUID idFromPath);

}