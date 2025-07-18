package xyz.xisyz.application.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.xisyz.adapter.producer.MailProducer;
import xyz.xisyz.application.dto.request.token.AuthChangeREQ;
import xyz.xisyz.application.dto.request.token.AuthREQ;
import xyz.xisyz.application.dto.request.token.OAuth2GoogleREQ;
import xyz.xisyz.application.dto.request.token.OAuthGitHubREQ;
import xyz.xisyz.application.dto.response.token.AuthRES;
import xyz.xisyz.domain.token.TokenService;

import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://xisyz.xyz"})
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Auth Controller", description = "Endpoints for authentication and authorization")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService service;
    private final MailProducer producer;

    @Operation(summary = "Login user", description = "Authenticates a user with username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully."),
            @ApiResponse(responseCode = "401", description = "Invalid password.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Email not confirmed.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthRES> loginUser(
            @Valid @RequestBody AuthREQ dto
    ) {
        AuthRES token = service.auth(dto.username(), dto.password());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @Operation(summary = "Login with Google", description = "Authenticates a user using Google OAuth2 token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid Google token.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/login/google")
    public ResponseEntity<AuthRES> loginGoogleUser(
            @Valid @RequestBody OAuth2GoogleREQ dto
    ) {
        AuthRES token = service.authWithGoogleAcc(dto.token());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @Operation(summary = "Login with GitHub", description = "Authenticates a user using GitHub OAuth2 token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/login/github")
    public ResponseEntity<AuthRES> loginGitHubUser(
            @Valid @RequestBody OAuthGitHubREQ dto
    ) {
        AuthRES token = service.authWithGitHubAcc(dto.code());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @Operation(summary = "Refresh token", description = "Generates a new access token using a refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New access token created."),
            @ApiResponse(responseCode = "400", description = "Invalid parameter.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Token not found, please log in again.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @GetMapping("/refresh")
    public ResponseEntity<AuthRES> refreshToken(
            @RequestParam("token") UUID refreshToken
    ) {
        AuthRES token = service.recreateToken(refreshToken);
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
    public ResponseEntity<Void> logoutUser(
            @Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken
    ) {
        String accessToken = bearerToken.replace("Bearer ", "");
        service.logout(accessToken);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Request user password change", description = "Generates a token for password change and sends it via email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Email sent successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Account not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/change-password")
    public ResponseEntity<String> requestPasswordChange(
            @Valid @RequestBody AuthChangeREQ dto
    ) {
        String jwt = service.generatePasswordChangeToken(dto.email());
        producer.publishAccountPasswordChangeMessage(dto.email(), jwt);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Request user email change", description = "Generates a token for email change and sends it via email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Email sent successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Account not found.", content = @Content(examples = {})),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(examples = {}))
    })
    @PostMapping("/change-email")
    public ResponseEntity<String> requestEmailChange(
            @Valid @RequestBody AuthChangeREQ dto
    ) {
        String jwt = service.generateEmailChangeToken(dto.email());
        producer.publishAccountEmailChangeMessage(dto.email(), jwt);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}