package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.application.dto.request.note.*;
import com.adm.lucas.microblog.application.dto.response.note.CreateNoteRES;
import com.adm.lucas.microblog.application.dto.response.note.DetailNoteRES;
import com.adm.lucas.microblog.application.dto.response.note.LowDetailNoteRES;
import com.adm.lucas.microblog.application.dto.response.page.PageRES;
import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.note.NoteService;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        List<String> tags = service.getAllTags();
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    @GetMapping("/private/tags")
    public ResponseEntity<List<String>> getAllUserTags(@RequestHeader("Authorization") String accessToken) {
        UUID idFromToken = getSubject(accessToken);
        List<String> tags = service.getAllUserTags(idFromToken);
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    @GetMapping("/search")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchPublicNotes(@PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                                       @NotBlank @RequestParam String q) {
        Page<LowDetailNoteRES> page = service.findPublicNotes(pageable, q).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @GetMapping("/private/search")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchPrivateNotes(@RequestHeader("Authorization") String accessToken,
                                                                        @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC)
                                                                        Pageable pageable,
                                                                        @NotBlank @RequestParam String q) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.findPrivateNotes(pageable, idFromToken, q).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @GetMapping("/search/tag")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchPublicNotesByTag(@PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                                            @NotBlank @RequestParam String q) {
        Page<LowDetailNoteRES> page = service.findPublicNotesByTag(pageable, q).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @GetMapping("/private/search/tag")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchPrivateNotesByTag(@RequestHeader("Authorization") String accessToken,
                                                                             @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                                             @NotBlank @RequestParam String q) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.findPrivateNotesByTag(pageable, idFromToken, q).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailNoteRES> getPublicNote(@PathVariable("id") UUID idFromPath) {
        Note note = service.getPublicNote(idFromPath);
        return ResponseEntity.status(HttpStatus.OK).body(new DetailNoteRES(note));
    }

    @GetMapping("/private/{id}")
    public ResponseEntity<DetailNoteRES> getPrivateNote(@RequestHeader("Authorization") String accessToken, @PathVariable("id") UUID idFromPath) {
        UUID idFromToken = getSubject(accessToken);
        Note note = service.getPrivateNote(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.OK).body(new DetailNoteRES(note));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<PageRES<LowDetailNoteRES>> getAllUserNotesByUsername(@PathVariable("username") String username,
                                                                               @PageableDefault(page = 0, size = 10, sort = {"modifiedAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<LowDetailNoteRES> page = service.getAllUserNotesByUsername(pageable, username).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @GetMapping("/private")
    public ResponseEntity<PageRES<LowDetailNoteRES>> getAllUserNotesById(@RequestHeader("Authorization") String accessToken,
                                                                         @PageableDefault(page = 0, size = 10, sort = {"modifiedAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.getAllUserNotesById(pageable, idFromToken).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

}