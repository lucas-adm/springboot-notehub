package com.adm.lucas.microblog.service;

import com.adm.lucas.microblog.model.Token;
import com.adm.lucas.microblog.model.User;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public interface SecurityService {

    Instant getExpirationTime(String tokenType);

    String generateToken(User user);

    String validateToken(String accessToken);

    Token auth(String username, String password) throws BadCredentialsException;

    void deleteAndFlush(Token token);

    Token recreateToken(UUID refreshToken) throws TokenExpiredException;

}