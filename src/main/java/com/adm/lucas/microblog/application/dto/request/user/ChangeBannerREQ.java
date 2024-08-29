package com.adm.lucas.microblog.application.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangeBannerREQ(
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @NotBlank(message = "Não pode ser vazio")
        String banner
) {
}