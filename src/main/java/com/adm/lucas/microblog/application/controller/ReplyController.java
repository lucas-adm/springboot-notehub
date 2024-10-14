package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.application.dto.request.reply.CreateReplyREQ;
import com.adm.lucas.microblog.application.dto.response.reply.CreateReplyRES;
import com.adm.lucas.microblog.application.dto.response.reply.DetailReplyRES;
import com.adm.lucas.microblog.application.dto.response.page.PageRES;
import com.adm.lucas.microblog.domain.reply.Reply;
import com.adm.lucas.microblog.domain.reply.ReplyService;
import com.auth0.jwt.JWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/notes/comments")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Reply Controller", description = "Endpoints for managing replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @Operation(summary = "Register a new reply to a comment", description = "Creates a new reply.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reply registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token or Note is closed.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Comment note found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/{id}/replies/new")
    @Transactional
    public ResponseEntity<CreateReplyRES> createReply(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @RequestBody @Valid CreateReplyREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        Reply reply = service.create(service.mapToReply(idFromToken, idFromPath, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateReplyRES(reply));
    }

    @Operation(summary = "Register a new reply to a another reply", description = "Creates a new reply.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reply registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token or Note is closed.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Reply note found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/replies/{id}/new")
    @Transactional
    public ResponseEntity<CreateReplyRES> createSelfReferenceReply(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @RequestBody @Valid CreateReplyREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        Reply reply = service.create(service.mapToSelfReference(idFromToken, idFromPath, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateReplyRES(reply));
    }

    @Operation(summary = "Change reply text", description = "Updates reply text field.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Reply text updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token or Note is closed.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Reply note found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PatchMapping("/replies/{id}/edit")
    @Transactional
    public ResponseEntity<Void> editReply(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @RequestBody @Valid CreateReplyREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.edit(idFromToken, idFromPath, dto.text());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Delete reply", description = "Removes a reply permanently.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reply deleted successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Reply not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @DeleteMapping("/replies/{id}/delete")
    @Transactional
    public ResponseEntity<Void> deleteReply(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.delete(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get replies", description = "Retrieves a replies page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Replies retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/{id}/replies")
    public ResponseEntity<PageRES<DetailReplyRES>> getReplies(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.ASC) Pageable pageable,
            @PathVariable("id") UUID idFromPath
    ) {
        Page<DetailReplyRES> page = service.getReplies(pageable, idFromPath).map(DetailReplyRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

}