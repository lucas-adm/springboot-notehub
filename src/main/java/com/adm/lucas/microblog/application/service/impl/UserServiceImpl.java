package com.adm.lucas.microblog.service.impl;

import com.adm.lucas.microblog.domain.model.User;
import com.adm.lucas.microblog.domain.repository.UserRepository;
import com.adm.lucas.microblog.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
    public void patchMessage(UUID idFromToken, String message) {
        User user = repository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        user.setMessage(message);
        repository.save(user);
    }

    @Override
    public void delete(UUID idFromToken) {
        User user = repository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        repository.delete(user);
    }

}