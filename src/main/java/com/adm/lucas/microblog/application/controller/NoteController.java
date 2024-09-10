package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.application.dto.request.note.ChangeTagsREQ;
import com.adm.lucas.microblog.application.dto.request.note.CreateNoteREQ;
import com.adm.lucas.microblog.application.dto.response.note.CreateNoteRES;
import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.note.NoteService;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @PostMapping("/new-note")
    @Transactional
    public ResponseEntity<CreateNoteRES> createNote(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody CreateNoteREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        Note note = service.create(service.mapToNote(idFromToken, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateNoteRES(note));
    }

    @PatchMapping("/{id}/change-tags")
    @Transactional
    public ResponseEntity<Void> changeNoteTags(@RequestHeader("Authorization") String accessToken, @PathVariable("id") UUID idFromPath, @Valid @RequestBody ChangeTagsREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeTags(idFromToken, idFromPath, dto.tags());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}/delete")
    @Transactional
    public ResponseEntity<Void> deleteNote(@RequestHeader("Authorization") String accessToken, @PathVariable("id") UUID idFromPath) {
        UUID idFromToken = getSubject(accessToken);
        service.delete(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}