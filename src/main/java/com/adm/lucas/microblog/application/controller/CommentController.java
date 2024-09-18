package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.application.dto.request.comment.CreateCommentREQ;
import com.adm.lucas.microblog.application.dto.response.comment.CreateCommentRES;
import com.adm.lucas.microblog.application.dto.response.comment.DetailCommentRES;
import com.adm.lucas.microblog.application.dto.response.page.PageRES;
import com.adm.lucas.microblog.domain.comment.Comment;
import com.adm.lucas.microblog.domain.comment.CommentService;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @PostMapping("/{id}/new-comment")
    @Transactional
    public ResponseEntity<CreateCommentRES> createComment(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody CreateCommentREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        Comment comment = service.create(service.mapToComment(idFromToken, idFromPath, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCommentRES(comment));
    }

    @PatchMapping("/comment/{id}/edit-comment")
    @Transactional
    public ResponseEntity<Void> editComment(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody CreateCommentREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.edit(idFromToken, idFromPath, dto.text());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/comment/{id}")
    @Transactional
    public ResponseEntity<Void> deleteComment(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.delete(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<PageRES<DetailCommentRES>> getComments(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("id") UUID idFromPath
    ) {
        Page<DetailCommentRES> page = service.getComments(pageable, idFromPath).map(DetailCommentRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

}