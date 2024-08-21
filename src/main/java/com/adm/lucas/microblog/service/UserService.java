package com.adm.lucas.microblog.service;

import com.adm.lucas.microblog.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {

    User create(User user);

    void validateUser(UUID idFromPath, UUID idFromToken);

    void patchMessage(UUID idFromPath, UUID idFromToken, String message);

}