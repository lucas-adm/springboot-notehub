package xyz.xisyz.application.dto.request.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangeMarkdownREQ(
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                flags = Pattern.Flag.DOTALL,
                message = "👀"
        )
        @NotBlank(message = "Não pode ser vazio")
        String markdown
) {
}