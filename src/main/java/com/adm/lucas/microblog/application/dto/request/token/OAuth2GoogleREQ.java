package com.adm.lucas.microblog.application.dto.request.token;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OAuth2GoogleREQ(
        @NotBlank
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        String jwt) {
}