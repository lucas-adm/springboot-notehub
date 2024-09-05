package com.adm.lucas.microblog.application.implementation;

import com.adm.lucas.microblog.domain.history.UserHistoryService;
import com.adm.lucas.microblog.domain.user.User;
import com.adm.lucas.microblog.domain.user.UserRepository;
import com.adm.lucas.microblog.domain.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserHistoryService historian;
    private final PasswordEncoder encoder;

    @SneakyThrows
    private <T> void changeField(UUID idFromToken, String field, Function<User, T> getter, Consumer<User> setter) {
        User user = repository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        if (!Objects.equals(user.getHost(), "Microblog") && (Objects.equals(field, "email") | Objects.equals(field, "password"))) {
            throw new UnknownHostException("Host não autorizado.");
        }
        T oldValue = getter.apply(user);
        setter.accept(user);
        repository.save(user);
        historian.setHistory(user, field, String.valueOf(oldValue), getter.apply(user).toString());
    }

    private void validateEmail(String email) {
        if (repository.findByEmail(email).isPresent()) throw new DataIntegrityViolationException("email");
    }

    private void validateUsername(String username) {
        if (repository.findByUsername(username).isPresent()) throw new DataIntegrityViolationException("username");
    }

    private void validateActiveField(boolean active) {
        if (!active) throw new EntityNotFoundException();
    }

    @Override
    public User create(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent() && repository.findByUsername(user.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("both");
        }
        validateEmail(user.getEmail());
        validateUsername(user.getUsername());
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public void active(UUID idFromToken) {
        changeField(idFromToken, "active", User::isActive, user -> user.setActive(true));
    }

    @Override
    public void changeProfileVisibility(UUID idFromToken) {
        changeField(idFromToken, "profile_private", User::isProfilePrivate, user -> user.setProfilePrivate(!user.isProfilePrivate()));
    }

    @Override
    public void changeEmail(UUID idFromToken, String email) {
        validateEmail(email);
        changeField(idFromToken, "email", User::getEmail, user -> user.setEmail(email.toLowerCase()));
    }

    @Override
    public void changeUsername(UUID idFromToken, String username) {
        validateUsername(username);
        changeField(idFromToken, "username", User::getUsername, user -> user.setUsername(username.toLowerCase()));
    }

    @Override
    public void changeDisplayName(UUID idFromToken, String displayName) {
        changeField(idFromToken, "display_name", User::getDisplayName, user -> user.setDisplayName(displayName));
    }

    @Override
    public void changeAvatar(UUID idFromToken, String avatar) {
        changeField(idFromToken, "avatar", User::getAvatar, user -> user.setAvatar(avatar));
    }

    @Override
    public void changeBanner(UUID idFromToken, String banner) {
        changeField(idFromToken, "banner", User::getBanner, user -> user.setBanner(banner));
    }

    @Override
    public void changeMessage(UUID idFromToken, String message) {
        changeField(idFromToken, "message", User::getMessage, user -> user.setMessage(message));
    }

    @Override
    public void changePassword(String email, String password) {
        UUID id = repository.findByEmail(email).orElseThrow(EntityNotFoundException::new).getId();
        changeField(id, "password", User::getPassword, user -> user.setPassword(encoder.encode(password)));
    }

    @Override
    public void delete(UUID idFromToken) {
        User user = repository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        repository.delete(user);
    }

    @Override
    public Page<User> getAllActiveUsers(Pageable pageable) {
        return repository.findAllByActiveTrue(pageable);
    }

    @Override
    public Page<User> findUser(Pageable pageable, String username, String displayName) {
        return repository.findByUsernameContainingIgnoreCaseAndActiveTrueOrDisplayNameContainingIgnoreCaseAndActiveTrue(pageable, username, displayName);
    }

    @Override
    public User getUser(String username) {
        User user = repository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        validateActiveField(user.isActive());
        return user;
    }

    @Override
    public List<String> getUserDisplayNameHistory(UUID id) {
        User user = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        validateActiveField(user.isActive());
        return historian.getLastFiveUserDisplayName(user);
    }

}