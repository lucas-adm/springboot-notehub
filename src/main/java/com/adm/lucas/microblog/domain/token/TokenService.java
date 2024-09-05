package com.adm.lucas.microblog.domain.token;

import com.adm.lucas.microblog.domain.user.User;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public interface TokenService {

    Instant getExpirationTime(String tokenType);

    String generateToken(User user);

    String generateChangePasswordToken(String email);

    String validateToken(String accessToken);

    Token auth(String username, String password) throws BadCredentialsException;

    Token authWithGoogleAcc(String jwt);

    Token authWithGitHubAcc(Integer id, String login, String avatar_url);

    Token recreateToken(UUID refreshToken) throws TokenExpiredException;

    void logout(String accessToken);

    void cleanExpiredTokens();

}