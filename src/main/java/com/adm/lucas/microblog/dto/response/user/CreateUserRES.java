package com.adm.lucas.microblog.dto.response.user;

import com.adm.lucas.microblog.model.User;
import com.adm.lucas.microblog.model.type.Role;

import java.util.UUID;

public record CreateUserRES(
        UUID id,
        String email,
        String username,
        String displayName,
        String message,
        Role role
) {
    public CreateUserRES(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getMessage(),
                user.getRole()
        );
    }
}