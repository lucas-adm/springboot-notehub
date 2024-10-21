package xyz.xisyz.application.controller.comment;

import xyz.xisyz.application.dto.request.comment.CreateCommentREQ;
import xyz.xisyz.application.dto.response.comment.CreateCommentRES;
import xyz.xisyz.application.dto.response.comment.DetailCommentRES;
import xyz.xisyz.application.dto.response.page.PageRES;
import xyz.xisyz.domain.comment.Comment;
import xyz.xisyz.domain.comment.CommentService;
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
@RequestMapping("/api/v1/notes")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Comment Controller", description = "Endpoints for managing comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @Operation(summary = "Register a new comment", description = "Creates a new comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token or Note is closed.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Note note found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/{id}/comments/new")
    @Transactional
    public ResponseEntity<CreateCommentRES> createComment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody CreateCommentREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        Comment comment = service.create(service.mapToComment(idFromToken, idFromPath, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCommentRES(comment));
    }

    @Operation(summary = "Change comment text", description = "Updates comment text field.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Comment text updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token or Note is closed.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Comment note found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PatchMapping("/comments/{id}/edit")
    @Transactional
    public ResponseEntity<Void> editComment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody CreateCommentREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.edit(idFromToken, idFromPath, dto.text());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Delete comment", description = "Removes a comment permanently.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Comment not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @DeleteMapping("/comments/{id}/delete")
    @Transactional
    public ResponseEntity<Void> deleteComment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.delete(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get comments", description = "Retrieves a comments page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/{id}/comments")
    public ResponseEntity<PageRES<DetailCommentRES>> getComments(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"repliesCount"}, direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("id") UUID idFromPath
    ) {
        Page<DetailCommentRES> page = service.getComments(pageable, idFromPath).map(DetailCommentRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

}