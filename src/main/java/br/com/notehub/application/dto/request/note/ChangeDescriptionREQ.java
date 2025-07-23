package br.com.notehub.application.dto.request.note;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangeDescriptionREQ(
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @Size(max = 255, message = "Além do limite")
        String description
) {
}