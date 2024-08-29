package com.adm.lucas.microblog.application.dto.response.user;

import java.util.UUID;

public record DetailUserRES(
        UUID id,
        String email,
        String username,
        String display_name,
        String avatar,
        String host,
        boolean profile_private,
        boolean sponsor,
        Long score
) {
}