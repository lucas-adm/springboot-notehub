package com.adm.lucas.microblog.service;

import com.adm.lucas.microblog.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {

    User create(User user);

    void patchMessage(UUID idFromToken, String message);

    void delete(UUID idFromToken);

}