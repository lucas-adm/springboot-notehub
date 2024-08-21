package com.adm.lucas.microblog.service.impl;

import com.adm.lucas.microblog.model.User;
import com.adm.lucas.microblog.model.type.Role;
import com.adm.lucas.microblog.repository.UserRepository;
import com.adm.lucas.microblog.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public User create(User user) {
        boolean emailIsPresent = repository.findByEmail(user.getEmail()).isPresent();
        boolean usernameIsPresent = repository.findByUsername(user.getUsername()).isPresent();
        if (emailIsPresent && usernameIsPresent) throw new DataIntegrityViolationException("both");
        if (usernameIsPresent) throw new DataIntegrityViolationException("username");
        if (emailIsPresent) throw new DataIntegrityViolationException("email");
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public void validateUser(UUID idFromPath, UUID idFromToken) {
        User entity = repository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        if (!Objects.equals(idFromPath, idFromToken) && !Objects.equals(entity.getRole(), Role.ADMIN)) {
            throw new AccessDeniedException("unauthorized");
        }
    }

    @Override
    public void patchMessage(UUID idFromPath, UUID idFromToken, String message) {
        validateUser(idFromPath, idFromToken);
        User user = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        user.setMessage(message);
        repository.save(user);
    }

}