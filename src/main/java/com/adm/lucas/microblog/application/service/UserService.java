package com.adm.lucas.microblog.application.service;

import com.adm.lucas.microblog.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {

    User create(User user);

    void active(UUID idFromToken);

    void changeProfileVisibility(UUID idFromToken);

    void changeEmail(UUID idFromToken, String email);

    void changeUsername(UUID idFromToken, String username);

    void changeDisplayName(UUID idFromToken, String displayName);

    void changeAvatar(UUID idFromToken, String avatar);

    void changeBanner(UUID idFromToken, String banner);

    void changeMessage(UUID idFromToken, String message);

    void changePassword(String email, String password);

    void delete(UUID idFromToken);

}