package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.domain.flame.FlameService;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flames")
@RequiredArgsConstructor
public class FlameController {

    private final FlameService service;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @PostMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> inflameNote(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        UUID idFromToken = getSubject(accessToken);
        service.inflame(idFromToken, idFromPath);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deflameNote(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") UUID idFromPath
    ) {
        service.deflame(idFromPath);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<UUID>> getFlames(
            @RequestHeader("Authorization") String accessToken
    ) {
        List<UUID> flames = service.getUserInflamedNotes(getSubject(accessToken));
        return ResponseEntity.status(HttpStatus.OK).body(flames);
    }

}