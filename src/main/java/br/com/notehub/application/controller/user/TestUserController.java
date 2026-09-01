package br.com.notehub.application.controller.user;

import br.com.notehub.application.dto.request.user.CreateUserREQ;
import br.com.notehub.application.dto.response.user.CreateUserRES;
import br.com.notehub.domain.user.User;
import br.com.notehub.domain.user.UserService;
import com.auth0.jwt.JWT;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Hidden
@Profile("e2e")
@RestController
@RequestMapping("/api/v1/test/users")
@RequiredArgsConstructor
public class TestUserController {

    private final UserService service;

    private UUID getSubject(String bearerToken) {
        if (bearerToken == null) return null;
        String idFromToken = JWT.decode(bearerToken.replace("Bearer ", "")).getSubject();
        return UUID.fromString(idFromToken);
    }

    @PostMapping("/register")
    public ResponseEntity<CreateUserRES> createUser(@Valid @RequestBody CreateUserREQ dto) {
        User user = service.create(dto.toUser());
        String jwt = service.generateActivationToken(user);
        service.activate(getSubject(jwt));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new CreateUserRES(user));
    }

}