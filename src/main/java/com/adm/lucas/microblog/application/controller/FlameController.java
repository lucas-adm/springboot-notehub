package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.domain.flame.FlameService;
import com.auth0.jwt.JWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flames")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Flame Controller", description = "Endpoints for managing user flames")
@RequiredArgsConstructor
public class FlameController {

    private final FlameService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @Operation(summary = "Register a new flame", description = "Inflames a note.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flame created successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Note note found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "409", description = "Only one flame per note."),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> inflameNote(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.inflame(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Delete flame", description = "Deflames a note.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flame deleted successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "404", description = "Note not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deflameNote(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.deflame(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get user inflamed notes", description = "Retrieves a list of flames.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Replies retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid token.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping
    public ResponseEntity<List<UUID>> getFlames(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken
    ) {
        List<UUID> flames = service.getUserInflamedNotes(getSubject(accessToken));
        return ResponseEntity.status(HttpStatus.OK).body(flames);
    }

}