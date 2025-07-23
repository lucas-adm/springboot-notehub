package br.com.notehub.domain.token;

import br.com.notehub.application.dto.response.token.AuthRES;
import br.com.notehub.domain.user.User;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public interface TokenService {

    Instant getExpirationTime(String tokenType);

    String generateToken(User user);

    String generateActivationToken(User user);

    String generatePasswordChangeToken(String email);

    String generateEmailChangeToken(String email);

    String validateToken(String accessToken);

    AuthRES auth(String username, String password) throws BadCredentialsException;

    AuthRES authWithGoogleAcc(String token);

    AuthRES authWithGitHubAcc(String code);

    AuthRES recreateToken(UUID refreshToken) throws TokenExpiredException;

    void logout(String accessToken);

    void cleanExpiredTokens();

}