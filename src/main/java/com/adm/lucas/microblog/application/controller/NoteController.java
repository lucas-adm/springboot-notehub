package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.application.dto.request.note.*;
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

    @PutMapping("/{id}/edit-note")
    @Transactional
    public ResponseEntity<Void> editNote(@RequestHeader("Authorization") String accessToken, @PathVariable("id") UUID idFromPath, @Valid @RequestBody EditNoteREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.edit(idFromToken, idFromPath, dto.title(), dto.tags(), dto.closed(), dto.hidden());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/{id}/change-title")
    @Transactional
    public ResponseEntity<Void> changeNoteTitle(@RequestHeader("Authorization") String accessToken, @PathVariable("id") UUID idFromPath, @Valid @RequestBody ChangeTitleREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeTitle(idFromToken, idFromPath, dto.title());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/{id}/change-markdown")
    @Transactional
    public ResponseEntity<Void> changeNoteMarkdown(@RequestHeader("Authorization") String accessToken, @PathVariable("id") UUID idFromPath, @Valid @RequestBody ChangeMarkdownReq dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeMarkdown(idFromToken, idFromPath, dto.markdown());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/{id}/change-status")
    @Transactional
    public ResponseEntity<Void> changeNoteStatus(@RequestHeader("Authorization") String accessToken, @PathVariable("id") UUID idFromPath) {
        UUID idFromToken = getSubject(accessToken);
        service.changeClosed(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/{id}/change-visibility")
    @Transactional
    public ResponseEntity<Void> changeNoteVisibility(@RequestHeader("Authorization") String accessToken, @PathVariable("id") UUID idFromPath) {
        UUID idFromToken = getSubject(accessToken);
        service.changeHidden(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
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