package xyz.xisyz.domain.token;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import xyz.xisyz.domain.user.User;

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

    Token auth(String username, String password) throws BadCredentialsException;

    Token authWithGoogleAcc(String token);

    Token authWithGitHubAcc(String code);

    Token recreateToken(UUID refreshToken) throws TokenExpiredException;

    void logout(String accessToken);

    void cleanExpiredTokens();

}