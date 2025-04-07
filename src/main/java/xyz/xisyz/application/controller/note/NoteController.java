package xyz.xisyz.application.controller.note;

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
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.xisyz.application.dto.request.note.*;
import xyz.xisyz.application.dto.response.note.CreateNoteRES;
import xyz.xisyz.application.dto.response.note.DetailNoteRES;
import xyz.xisyz.application.dto.response.note.LowDetailNoteRES;
import xyz.xisyz.application.dto.response.page.PageRES;
import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.note.NoteService;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://xisyz.xyz"})
@RequestMapping("/api/v1/notes")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Note Controller", description = "Endpoints for managing notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @Operation(summary = "Register a new note", description = "Creates a new note public or private.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/new-note")
    @Transactional
    public ResponseEntity<CreateNoteRES> createNote(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody CreateNoteREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        Note note = service.create(service.mapToNote(idFromToken, dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateNoteRES(note));
    }

    @Operation(summary = "Edit note fields", description = "Updates note.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Note updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token."),
            @ApiResponse(responseCode = "404", description = "Note not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PutMapping("/{id}/edit-note")
    @Transactional
    public ResponseEntity<Void> editNote(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody EditNoteREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.edit(idFromToken, idFromPath, dto.title(), dto.description(), dto.tags(), dto.closed(), dto.hidden());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Change note title", description = "Changes the note title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Note updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token."),
            @ApiResponse(responseCode = "404", description = "Note not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PatchMapping("/{id}/change-title")
    @Transactional
    public ResponseEntity<Void> changeNoteTitle(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody ChangeTitleREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.changeTitle(idFromToken, idFromPath, dto.title());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Change note description", description = "Changes the note description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Note updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token."),
            @ApiResponse(responseCode = "404", description = "Note not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PatchMapping("/{id}/change-description")
    @Transactional
    public ResponseEntity<Void> changeNoteDescription(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody ChangeDescriptionREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.changeDescription(idFromToken, idFromPath, dto.description());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Change note content", description = "Changes the note markdown content.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Note markdown changed successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token."),
            @ApiResponse(responseCode = "404", description = "Note not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PatchMapping("/{id}/change-markdown")
    @Transactional
    public ResponseEntity<Void> changeNoteMarkdown(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody ChangeMarkdownReq dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.changeMarkdown(idFromToken, idFromPath, dto.markdown());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Change note status", description = "Set note status to closed true or false depending on current value.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Note status changed successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token."),
            @ApiResponse(responseCode = "404", description = "Note not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PatchMapping("/{id}/change-status")
    @Transactional
    public ResponseEntity<Void> changeNoteStatus(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.changeClosed(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Change note visibility", description = "Set note hidden to true or false depending on current value.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Note visibility changed successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token."),
            @ApiResponse(responseCode = "404", description = "Note not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PatchMapping("/{id}/change-visibility")
    @Transactional
    public ResponseEntity<Void> changeNoteVisibility(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.changeHidden(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Change note tags", description = "Set a array of strings as new tag names to user's note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Note tags updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid token."),
            @ApiResponse(responseCode = "404", description = "Note not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PatchMapping("/{id}/change-tags")
    @Transactional
    public ResponseEntity<Void> changeNoteTags(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath,
            @Valid @RequestBody ChangeTagsREQ dto
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.changeTags(idFromToken, idFromPath, dto.tags());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Delete note", description = "Deletes a note permanently.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note deleted successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Note not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @DeleteMapping("/{id}/delete")
    @Transactional
    public ResponseEntity<Void> deleteNote(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.delete(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get all tags", description = "Retrieves a paginated list of all used tags.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tags retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        List<String> tags = service.getAllTags();
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    @Operation(summary = "Get all tags used by user", description = "Retrieves a paginated list of all used tags by user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Used tags by user retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/private/tags")
    public ResponseEntity<List<String>> getAllUserTags(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken
    ) {
        UUID idFromToken = getSubject(accessToken);
        List<String> tags = service.getAllUserTags(idFromToken);
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    @Operation(summary = "Search for notes", description = "Searches notes by title or tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/search")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchPublicNotes(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"flamesCount"}, direction = Sort.Direction.DESC) Pageable pageable,
            @NotBlank @RequestParam String q
    ) {
        Page<LowDetailNoteRES> page = service.findPublicNotes(pageable, q).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @Operation(summary = "Search for user notes", description = "Searches user notes by title or tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/private/search")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchPrivateNotes(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC)
            Pageable pageable,
            @NotBlank @RequestParam String q
    ) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.findPrivateNotes(pageable, idFromToken, q).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @Operation(summary = "Search for notes", description = "Searches notes by tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/search/tag")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchPublicNotesByTag(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"flamesCount"}, direction = Sort.Direction.DESC) Pageable pageable,
            @NotBlank @RequestParam String q
    ) {
        Page<LowDetailNoteRES> page = service.findPublicNotesByTag(pageable, q).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @Operation(summary = "Search for user notes", description = "Searches user notes by tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/private/search/tag")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchPrivateNotesByTag(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            @NotBlank @RequestParam String q
    ) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.findPrivateNotesByTag(pageable, idFromToken, q).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @Operation(
            summary = "Search user notes by multiple criteria",
            description = """
                     Searches notes of a specific user combining optional filters:
                     - Username (path parameter)
                     - Text query (q) for title
                     - Tag name (tag)
                     - Type (open, closed and hidden)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/{username}/specs")
    public ResponseEntity<PageRES<LowDetailNoteRES>> searchUserNotesBySpecs(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @ParameterObject @PageableDefault(page = 0, size = 25, sort = {"modifiedAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("username") String username,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String type
    ) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.findUserNotesBySpecs(idFromToken, pageable, username, q, tag, type).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @Operation(summary = "Get a public note details", description = "Retrieves detailed information about a note by their uuid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "Note not found", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetailNoteRES> getPublicNote(
            @PathVariable("id") UUID idFromPath
    ) {
        Note note = service.getPublicNote(idFromPath);
        return ResponseEntity.status(HttpStatus.OK).body(new DetailNoteRES(note));
    }

    @Operation(summary = "Get a private note details", description = "Retrieves detailed information about a note by their uuid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Note not found", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/private/{id}")
    public ResponseEntity<DetailNoteRES> getPrivateNote(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        Note note = service.getPrivateNote(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.OK).body(new DetailNoteRES(note));
    }

    @Operation(
            summary = "Get details of a note created by a private profile user",
            description = "Both the requester and the note's owner must follow each other to access the note."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note details retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "No bidirectional follow relationship between users or invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Note not found", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/private/mutual/{id}")
    public ResponseEntity<DetailNoteRES> getPrivateFollowingUserNote(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        Note note = service.getPrivateFollowingUserNote(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.OK).body(new DetailNoteRES(note));
    }

    @Operation(summary = "Search for notes", description = "Searches notes by user username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/user/{username}")
    public ResponseEntity<PageRES<LowDetailNoteRES>> getAllUserNotesByUsername(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"flamesCount"}, direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("username") String username
    ) {
        Page<LowDetailNoteRES> page = service.getAllUserNotesByUsername(pageable, username).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @Operation(summary = "Search for notes", description = "Searches notes by user id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/private")
    public ResponseEntity<PageRES<LowDetailNoteRES>> getAllUserNotesById(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"modifiedAt"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.getAllUserNotesById(pageable, idFromToken).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @Operation(
            summary = "Get notes from followed users",
            description = "Retrieves a paginated list of notes from users that the authenticated user is following."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "403", description = "Access token is invalid or missing.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/private/following")
    public ResponseEntity<PageRES<LowDetailNoteRES>> getAllFollowingUsersNotes(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.getAllFollowedUsersNotes(pageable, idFromToken).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

    @Operation(
            summary = "Get notes from a user with mutual connections",
            description = "Retrieves a paginated list of notes from a user that the authenticated user is following."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notes retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pageable criteria.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "403", description = "Access token is invalid or missing.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/private/following/{username}")
    public ResponseEntity<PageRES<LowDetailNoteRES>> getAllFollowingUserNotes(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("username") String username,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID idFromToken = getSubject(accessToken);
        Page<LowDetailNoteRES> page = service.getAllFollowedUserNotes(pageable, idFromToken, username).map(LowDetailNoteRES::new);
        return ResponseEntity.status(HttpStatus.OK).body(new PageRES<>(page));
    }

}