package xyz.xisyz.application.dto.request.note;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;

public record ChangeDescriptionREQ(
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @Max(value = 255, message = "Além do limite")
        String description
) {
}