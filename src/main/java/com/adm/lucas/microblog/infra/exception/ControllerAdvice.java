package com.adm.lucas.microblog.infra.exception;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {

    private List<FieldError> errors = new ArrayList<>();

    @ModelAttribute
    private void clearList() {
        errors.clear();
    }

    private record CustomResponse(String field, String message) {
        public CustomResponse(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<List<CustomResponse>> handleMethdArgumentNotValidException(MethodArgumentNotValidException ex) {
        errors = ex.getFieldErrors();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.stream().map(CustomResponse::new).toList());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<List<CustomResponse>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        switch (ex.getMessage()) {
            case "both":
                errors.add(new FieldError("user", "email", "Email já existe."));
                errors.add(new FieldError("user", "username", "Nome já existe."));
                break;
            case "email":
                errors.add(new FieldError("user", "email", "Email já existe."));
                break;
            case "username":
                errors.add(new FieldError("user", "username", "Nome já existe."));
                break;
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errors.stream().map(CustomResponse::new).toList());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<HttpStatus> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<List<CustomResponse>> handleBadCredentialsException(BadCredentialsException ex) {
        return switch (ex.getMessage()) {
            case "username" -> {
                errors.add(new FieldError("user", "username", "Nome não existe."));
                yield ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors.stream().map(CustomResponse::new).toList());
            }
            case "password" -> {
                errors.add(new FieldError("user", "password", "Senha incorreta."));
                yield ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors.stream().map(CustomResponse::new).toList());
            }
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        };
    }

    @ExceptionHandler(DisabledException.class)
    private ResponseEntity<List<CustomResponse>> handlerDisabledException(DisabledException ex) {
        errors.add(new FieldError("user", "username", ex.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors.stream().map(CustomResponse::new).toList());
    }

    @ExceptionHandler(JWTCreationException.class)
    private ResponseEntity<List<CustomResponse>> handleJWTCreationException(JWTCreationException ex) {
        errors.add(new FieldError("token", "token", ex.getMessage()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors.stream().map(CustomResponse::new).toList());
    }

    @ExceptionHandler(JWTDecodeException.class)
    private ResponseEntity<List<CustomResponse>> handleJWTDecodeException(JWTDecodeException ex) {
        errors.add(new FieldError("token", "oauth2_token", ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.stream().map(CustomResponse::new).toList());
    }

    @ExceptionHandler(TokenExpiredException.class)
    private ResponseEntity<List<CustomResponse>> handleTokenExpiredException(TokenExpiredException ex) {
        errors.add(new FieldError("token", "refresh_token", ex.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors.stream().map(CustomResponse::new).toList());
    }

}