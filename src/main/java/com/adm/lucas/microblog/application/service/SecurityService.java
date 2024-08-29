package com.adm.lucas.microblog.application.service;

import com.adm.lucas.microblog.domain.model.Token;
import com.adm.lucas.microblog.domain.model.User;
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

    Token authWithGoogleAcc(String jwt);

    Token authWithGitHubAcc(Integer id, String login, String avatar_url);

    void deleteAndFlush(Token token);

    Token recreateToken(UUID refreshToken) throws TokenExpiredException;

    void logout(String accessToken);

    String generateChangePasswordToken(String email);

}