package com.adm.lucas.microblog.application.controller;

import com.adm.lucas.microblog.adapter.producer.MailProducer;
import com.adm.lucas.microblog.application.dto.request.token.AuthREQ;
import com.adm.lucas.microblog.application.dto.request.token.OAuth2GitHubREQ;
import com.adm.lucas.microblog.application.dto.request.token.OAuth2GoogleREQ;
import com.adm.lucas.microblog.application.dto.request.token.RecoverPasswordREQ;
import com.adm.lucas.microblog.application.dto.response.token.AuthRES;
import com.adm.lucas.microblog.application.service.SecurityService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService service;
    private final MailProducer producer;

    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(@Valid @RequestBody RecoverPasswordREQ dto) {
        String jwt = service.generateChangePasswordToken(dto.email());
        producer.publishAccountRecoveryMessage(dto.email(), jwt);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<AuthRES> loginUser(@Valid @RequestBody AuthREQ dto) throws LoginException {
        AuthRES token = new AuthRES(service.auth(dto.username(), dto.password()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
    }

    @PostMapping("/login/oauth2/google")
    @Transactional
    public ResponseEntity<AuthRES> loginGoogleUser(@Valid @RequestBody OAuth2GoogleREQ dto) {
        AuthRES token = new AuthRES(service.authWithGoogleAcc(dto.jwt()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
    }

    @PostMapping("/login/oauth2/github")
    @Transactional
    public ResponseEntity<AuthRES> loginGitHubUser(@Valid @RequestBody OAuth2GitHubREQ dto) {
        AuthRES token = new AuthRES(service.authWithGitHubAcc(dto.id(), dto.login(), dto.avatar_url()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
    }

    @GetMapping("/refresh")
    @Transactional
    public ResponseEntity<AuthRES> refreshToken(@RequestParam("refreshToken") UUID refreshToken) {
        AuthRES token = new AuthRES(service.recreateToken(refreshToken));
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @DeleteMapping("/logout")
    @Transactional
    public ResponseEntity<Void> logoutUser(@RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.replace("Bearer ", "");
        service.logout(accessToken);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}