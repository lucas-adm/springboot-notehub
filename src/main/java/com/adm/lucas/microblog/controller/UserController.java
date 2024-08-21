package com.adm.lucas.microblog.controller;

import com.adm.lucas.microblog.dto.request.user.CreateUserREQ;
import com.adm.lucas.microblog.dto.request.user.PatchMsgREQ;
import com.adm.lucas.microblog.dto.response.user.CreateUserRES;
import com.adm.lucas.microblog.model.User;
import com.adm.lucas.microblog.service.impl.UserServiceImpl;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/microblog/api/v1.0/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl service;

    private UUID getSubject(String accessToken) {
        String idFromToken = JWT.decode(accessToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<CreateUserRES> createUser(@Valid @RequestBody CreateUserREQ dto) {
        User user = service.create(dto.toUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserRES(user));
    }

    @PatchMapping("/{id}/message")
    @Transactional
    public ResponseEntity<Void> patchMessage(@PathVariable("id") UUID idFromPath, @RequestHeader("Authorization") String accessToken, @Valid @RequestBody PatchMsgREQ dto) {
        UUID idFromToken = getSubject(accessToken);
        service.patchMessage(idFromPath, idFromToken, dto.message());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}