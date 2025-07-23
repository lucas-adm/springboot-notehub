package br.com.notehub.application.scheduler;

import br.com.notehub.domain.token.TokenService;
import br.com.notehub.domain.user.UserService;
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