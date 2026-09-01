package br.com.notehub.application.controller.auth;

import br.com.notehub.application.dto.request.token.AuthREQ;
import br.com.notehub.application.dto.response.auth.AuthRES;
import br.com.notehub.domain.auth.AuthService;
import br.com.notehub.domain.user.User;
import br.com.notehub.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Profile("e2e")
@RestController
@RequestMapping("/api/v1/test/auth")
@RequiredArgsConstructor
public class TestAuthController {

    private final UserRepository uRepository;
    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<AuthRES> loginUser(
            HttpServletRequest request,
            @Valid @RequestBody AuthREQ dto
    ) {
        User user = uRepository.findByUsername(dto.identifier()).orElseThrow();
        if (!user.isActive()) user.setActive(true);
        AuthRES token = service.auth(request, dto.identifier(), dto.password());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

}