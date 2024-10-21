package xyz.xisyz.application.controller.auth;

import xyz.xisyz.adapter.producer.MailProducer;
import xyz.xisyz.application.dto.request.token.AuthREQ;
import xyz.xisyz.application.dto.request.token.OAuth2GitHubREQ;
import xyz.xisyz.application.dto.request.token.OAuth2GoogleREQ;
import xyz.xisyz.application.dto.request.token.RecoverPasswordREQ;
import xyz.xisyz.application.dto.response.token.AuthRES;
import xyz.xisyz.domain.token.TokenService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Auth Controller", description = "Endpoints for authentication and authorization")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService service;
    private final MailProducer producer;

    @Operation(summary = "Recover password", description = "Generates a token for password recovery and sends it via email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recovery email sent successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Account not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(
            @Valid @RequestBody RecoverPasswordREQ dto
    ) {
        String jwt = service.generateChangePasswordToken(dto.email());
        producer.publishAccountRecoveryMessage(dto.email(), jwt);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Login user", description = "Authenticates a user with username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User authenticated successfully."),
            @ApiResponse(responseCode = "401", description = "Invalid password.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Email not confirmed.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/login")
    @Transactional
    public ResponseEntity<AuthRES> loginUser(
            @Valid @RequestBody AuthREQ dto
    ) {
        AuthRES token = new AuthRES(service.auth(dto.username(), dto.password()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
    }

    @Operation(summary = "Login with Google", description = "Authenticates a user using Google OAuth2 token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User authenticated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid Google token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/login/oauth2/google")
    @Transactional
    public ResponseEntity<AuthRES> loginGoogleUser(
            @Valid @RequestBody OAuth2GoogleREQ dto
    ) {
        AuthRES token = new AuthRES(service.authWithGoogleAcc(dto.jwt()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
    }

    @Operation(summary = "Login with GitHub", description = "Authenticates a user using GitHub OAuth2 token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User authenticated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/login/oauth2/github")
    @Transactional
    public ResponseEntity<AuthRES> loginGitHubUser(
            @Valid @RequestBody OAuth2GitHubREQ dto
    ) {
        AuthRES token = new AuthRES(service.authWithGitHubAcc(dto.id(), dto.login(), dto.avatar_url()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
    }

    @Operation(summary = "Refresh token", description = "Generates a new access token using a refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New access token created."),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Token not found, please log in again.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/refresh")
    @Transactional
    public ResponseEntity<AuthRES> refreshToken(
            @RequestParam("token") UUID refreshToken
    ) {
        AuthRES token = new AuthRES(service.recreateToken(refreshToken));
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @Operation(summary = "Logout user", description = "Logs out the user by invalidating the access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User logged out successfully."),
            @ApiResponse(responseCode = "403", description = "Invalid token."),
            @ApiResponse(responseCode = "404", description = "Invalid access token."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @DeleteMapping("/logout")
    @Transactional
    public ResponseEntity<Void> logoutUser(
            @Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken
    ) {
        String accessToken = bearerToken.replace("Bearer ", "");
        service.logout(accessToken);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}