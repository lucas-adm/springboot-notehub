package com.adm.lucas.microblog.domain.note;

import com.adm.lucas.microblog.application.dto.request.note.CreateNoteREQ;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface NoteService {

    Note map(UUID idFromToken, CreateNoteREQ req);

    Note create(Note note);

}