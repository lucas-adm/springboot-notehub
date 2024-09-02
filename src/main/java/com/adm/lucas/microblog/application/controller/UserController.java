package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.adapter.producer.MailProducer;
import com.adm.lucas.microblog.application.dto.request.user.*;
import com.adm.lucas.microblog.application.dto.response.user.CreateUserRES;
import com.adm.lucas.microblog.application.service.impl.UserServiceImpl;
import com.adm.lucas.microblog.domain.model.User;
import com.auth0.jwt.JWT;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
public class UserController {

    @Value("${frontend.host}")
    private String domain;

    private final UserServiceImpl service;
    private final MailProducer producer;

    private UUID getSubject(String bearerToken) {
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<CreateUserRES> createUser(@Valid @RequestBody CreateUserREQ dto) {
        User user = service.create(dto.toUser());
        producer.publishAccountActivationMessage(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserRES(user));
    }

    @GetMapping("/active/{id}")
    @Transactional
    public ResponseEntity<Void> activeUser(@PathVariable("id") UUID id) {
        service.active(id);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(String.format("%s/greetings", domain))).build();
    }

    @PatchMapping("/profile/change-visibility")
    @Transactional
    public ResponseEntity<Void> changeProfileVisibility(@RequestHeader("Authorization") String accessToken) {
        UUID idFromToken = getSubject(accessToken);
        service.changeProfileVisibility(idFromToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/email")
    @Transactional
    public ResponseEntity<Void> patchEmail(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody ChangeEmailREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeEmail(idFromToken, dto.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/username")
    @Transactional
    public ResponseEntity<Void> patchUsername(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody ChangeUsernameREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeUsername(idFromToken, dto.username());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/display-name")
    @Transactional
    public ResponseEntity<Void> patchDisplayName(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody ChangeDisplayNameREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeDisplayName(idFromToken, dto.displayName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/profile/change-picture")
    @Transactional
    public ResponseEntity<Void> patchAvatar(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody ChangeAvatarREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeAvatar(idFromToken, dto.avatar());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/profile/change-banner")
    @Transactional
    public ResponseEntity<Void> patchBanner(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody ChangeBannerREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeBanner(idFromToken, dto.banner());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/message")
    @Transactional
    public ResponseEntity<Void> patchMessage(@RequestHeader("Authorization") String accessToken, @Valid @RequestBody ChangeMessageREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.changeMessage(idFromToken, dto.message());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/reset-password")
    @Transactional
    public ResponseEntity<Void> patchPassword(@RequestParam String token, @Valid @RequestBody ChangePasswordREQ dto) {
        String emailFromToken = JWT.decode(token.replace("Bearer ", "")).getSubject();
        service.changePassword(emailFromToken, dto.password());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String accessToken) {
        UUID idFromToken = getSubject(accessToken);
        service.delete(idFromToken);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}