package br.com.notehub.application.validation.constraints;

import br.com.notehub.application.validation.NoForbiddenWordsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoForbiddenWordsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoForbiddenWords {

    String message() default "Not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}