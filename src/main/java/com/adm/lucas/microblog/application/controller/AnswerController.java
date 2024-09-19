package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.application.dto.request.answer.CreateAnswerREQ;
import com.adm.lucas.microblog.application.dto.response.answer.CreateAnswerRES;
import com.adm.lucas.microblog.application.dto.response.answer.DetailAnswerRES;
import com.adm.lucas.microblog.application.dto.response.page.PageRES;
import com.adm.lucas.microblog.domain.answer.Answer;
import com.adm.lucas.microblog.domain.answer.AnswerService;
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
public class AnswerController {

    private final AnswerService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @PostMapping("/{id}/answers/new")
    @Transactional
    public ResponseEntity<CreateAnswerRES> createAnswer(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @RequestBody @Valid CreateAnswerREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        Answer answer = service.create(service.mapToAnswer(idFromToken, idFromPath, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAnswerRES(answer));
    }

    @PatchMapping("/answers/{id}/edit")
    @Transactional
    public ResponseEntity<Void> editAnswer(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @RequestBody @Valid CreateAnswerREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.edit(idFromToken, idFromPath, dto.text());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/answers/{id}/delete")
    @Transactional
    public ResponseEntity<Void> deleteAnswer(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.delete(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/answers")
    public ResponseEntity<PageRES<DetailAnswerRES>> getAnswers(
            @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("id") UUID idFromPath
    ) {
        Page<DetailAnswerRES> page = service.getAnswers(pageable, idFromPath).map(DetailAnswerRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

}