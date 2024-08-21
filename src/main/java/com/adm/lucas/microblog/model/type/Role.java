package com.adm.lucas.microblog.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("admin"),
    BASIC("basic");
    private final String role;
}