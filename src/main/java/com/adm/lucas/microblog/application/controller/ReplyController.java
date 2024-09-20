package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.application.dto.request.reply.CreateReplyREQ;
import com.adm.lucas.microblog.application.dto.response.reply.CreateReplyRES;
import com.adm.lucas.microblog.application.dto.response.reply.DetailReplyRES;
import com.adm.lucas.microblog.application.dto.response.page.PageRES;
import com.adm.lucas.microblog.domain.reply.Reply;
import com.adm.lucas.microblog.domain.reply.ReplyService;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes/comments")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @PostMapping("/{id}/replies/new")
    @Transactional
    public ResponseEntity<CreateReplyRES> createReply(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @RequestBody @Valid CreateReplyREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        Reply reply = service.create(service.mapToReply(idFromToken, idFromPath, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateReplyRES(reply));
    }

    @PostMapping("/replies/{id}/new")
    @Transactional
    public ResponseEntity<CreateReplyRES> createSelfReferenceReply(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @RequestBody @Valid CreateReplyREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        Reply reply = service.create(service.mapToSelfReference(idFromToken, idFromPath, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateReplyRES(reply));
    }

    @PatchMapping("/replies/{id}/edit")
    @Transactional
    public ResponseEntity<Void> editReply(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @RequestBody @Valid CreateReplyREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.edit(idFromToken, idFromPath, dto.text());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/replies/{id}/delete")
    @Transactional
    public ResponseEntity<Void> deleteReply(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.delete(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<PageRES<DetailReplyRES>> getReplies(
            @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("id") UUID idFromPath
    ) {
        Page<DetailReplyRES> page = service.getReplies(pageable, idFromPath).map(DetailReplyRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

}