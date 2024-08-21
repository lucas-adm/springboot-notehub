package com.adm.lucas.microblog.service.impl;

import com.adm.lucas.microblog.model.Token;
import com.adm.lucas.microblog.model.User;
import com.adm.lucas.microblog.repository.TokenRepository;
import com.adm.lucas.microblog.repository.UserRepository;
import com.adm.lucas.microblog.service.SecurityService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
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
        boolean matches = encoder.matches(password, user.getPassword());
        if (!matches) throw new BadCredentialsException("password");

        Optional<Token> token = repository.findByUserId(user.getId());
        token.ifPresent(this::deleteAndFlush);

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

}