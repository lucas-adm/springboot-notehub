package com.adm.lucas.microblog.application.service.impl;

import com.adm.lucas.microblog.application.service.SecurityService;
import com.adm.lucas.microblog.domain.model.Token;
import com.adm.lucas.microblog.domain.model.User;
import com.adm.lucas.microblog.domain.repository.TokenRepository;
import com.adm.lucas.microblog.domain.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    @Value("${api.security.token.secret}")
    private String secret;

    private final TokenRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public Instant getExpirationTime(String tokenType) {
        return switch (tokenType) {
            case "refresh" -> LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00"));
            case "access" -> LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.of("-03:00"));
            default -> null;
        };
    }

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Microblog")
                    .withSubject(String.valueOf(user.getId()))
                    .withExpiresAt(getExpirationTime("access"))
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new JWTCreationException("Error while creating token", ex);
        }
    }

    @Override
    public String validateToken(String accessToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("Microblog")
                    .build()
                    .verify(accessToken)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException("Error while validating token: ", ex);
        }
    }

    @Override
    public Token auth(String username, String password) throws BadCredentialsException {
        User user = userRepository.findByUsername(username.toLowerCase()).orElseThrow(() -> new BadCredentialsException("username"));
        if (!user.isActive()) throw new DisabledException("Email não confirmado");

        boolean matches = encoder.matches(password, user.getPassword());
        if (!matches) throw new BadCredentialsException("password");

        repository.findByUserId(user.getId()).ifPresent(this::deleteAndFlush);

        String accessToken = generateToken(user);
        Instant expiresAt = getExpirationTime("refresh");
        return repository.save(new Token(user, accessToken, expiresAt));
    }

    @Override
    public Token authWithGoogleAcc(String jwt) {
        try {
            DecodedJWT decoded = JWT.decode(jwt);
            String email = decoded.getClaims().get("email").asString();
            String givenName = decoded.getClaims().get("given_name").asString();
            String sub = decoded.getClaims().get("sub").asString().substring(0, 8);
            String username = String.format("%s%s", givenName, sub);
            String displayName = decoded.getClaims().get("name").asString();
            String avatar = decoded.getClaims().get("picture").asString();

            User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(new User(email, username, displayName, avatar)));
            repository.findByUserId(user.getId()).ifPresent(this::deleteAndFlush);

            String accessToken = generateToken(user);
            Instant expiresAt = getExpirationTime("refresh");

            return repository.save(new Token(user, accessToken, expiresAt));
        } catch (JWTDecodeException exception) {
            throw new JWTDecodeException("Formato inválido");
        }
    }

    @Override
    public Token authWithGitHubAcc(Integer id, String login, String avatar_url) {
        String username = String.format("%s%s", login, id);

        User user = userRepository.findByUsername(username).orElseGet(() -> userRepository.save(new User(username, login, avatar_url)));
        repository.findByUserId(user.getId()).ifPresent(this::deleteAndFlush);

        String accessToken = generateToken(user);
        Instant expiresAt = getExpirationTime("refresh");

        return repository.save(new Token(user, accessToken, expiresAt));
    }

    @Override
    public void deleteAndFlush(Token token) {
        repository.delete(token);
        repository.flush();
    }

    @Override
    public Token recreateToken(UUID refreshToken) throws TokenExpiredException {
        Token token = repository.findById(refreshToken).orElseThrow(EntityNotFoundException::new);

        Instant now = LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"));
        if (token.getExpiresAt().isBefore(now)) {
            throw new TokenExpiredException("Refresh Token expirado.", token.getExpiresAt());
        }

        User user = token.getUser();
        String jwt = generateToken(user);
        Instant expiresAt = getExpirationTime("refresh");

        deleteAndFlush(token);
        return repository.save(new Token(user, jwt, expiresAt));
    }

    @Override
    public void logout(String accessToken) {
        repository.findByAccessToken(accessToken).ifPresent(repository::delete);
    }

    @Override
    public String generateChangePasswordToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Microblog")
                    .withSubject(email)
                    .withExpiresAt(getExpirationTime("access"))
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new JWTCreationException("Error while creating token", ex);
        }
    }

}