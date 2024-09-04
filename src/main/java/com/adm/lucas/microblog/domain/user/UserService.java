package com.adm.lucas.microblog.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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

    Page<User> getAllActiveUsers(Pageable pageable);

    Page<User> findUser(Pageable pageable, String username, String displayName);

    User getUser(String username);

    List<String> getUserDisplayNameHistory(UUID id);

}