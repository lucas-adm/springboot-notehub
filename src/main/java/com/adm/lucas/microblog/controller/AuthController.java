package com.adm.lucas.microblog.controller;

import com.adm.lucas.microblog.dto.request.token.AuthREQ;
import com.adm.lucas.microblog.dto.response.token.AuthRES;
import com.adm.lucas.microblog.service.SecurityService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.UUID;

@RestController
@RequestMapping("/microblog/api/v1.0/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService service;

    @PostMapping("/login")
    public ResponseEntity<AuthRES> loginUser(@Valid @RequestBody AuthREQ dto) throws LoginException {
        AuthRES token = new AuthRES(service.auth(dto.username(), dto.password()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
    }

    @PostMapping("/refresh")
    @Transactional
    public ResponseEntity<AuthRES> refreshToken(@RequestParam("refreshToken") UUID refreshToken) {
        AuthRES token = new AuthRES(service.recreateToken(refreshToken));
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

}