package xyz.xisyz.application.scheduler;

import xyz.xisyz.domain.token.TokenService;
import xyz.xisyz.domain.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final UserService userService;
    private final TokenService tokenService;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanTokens() {
        tokenService.cleanExpiredTokens();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanUsers() {
        userService.cleanUsersWithExpiredActivationTime();
    }

}