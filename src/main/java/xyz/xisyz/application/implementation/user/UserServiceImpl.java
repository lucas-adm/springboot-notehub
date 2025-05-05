package xyz.xisyz.application.implementation.user;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import xyz.xisyz.application.counter.Counter;
import xyz.xisyz.application.dto.notification.MessageNotification;
import xyz.xisyz.domain.history.UserHistoryService;
import xyz.xisyz.domain.notification.NotificationService;
import xyz.xisyz.domain.token.TokenService;
import xyz.xisyz.domain.user.User;
import xyz.xisyz.domain.user.UserRepository;
import xyz.xisyz.domain.user.UserService;
import xyz.xisyz.infra.exception.CustomExceptions;

import java.net.UnknownHostException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserHistoryService historian;
    private final NotificationService notifier;
    private final TokenService tokenService;
    private final PasswordEncoder encoder;
    private final Counter counter;

    @SneakyThrows
    private <T> void changeField(UUID idFromToken, String field, Function<User, T> getter, Consumer<User> setter) {
        User user = repository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        if (!Objects.equals(user.getHost(), "XYZ") && (Objects.equals(field, "email") | Objects.equals(field, "password"))) {
            throw new UnknownHostException("Host não autorizado.");
        }
        T oldValue = getter.apply(user);
        setter.accept(user);
        T newValue = getter.apply(user);
        if (Objects.equals(oldValue, newValue)) return;
        repository.save(user);
        historian.setHistory(user, field, String.valueOf(oldValue), newValue.toString());
    }

    private void validateEmail(UUID idFromToken, String email) {
        repository.findByEmail(email).ifPresent((stored) -> {
            repository.findById(idFromToken).ifPresent((user) -> {
                        if (!Objects.equals(stored, user)) {
                            throw new DataIntegrityViolationException("email");
                        }
                    }
            );
        });
    }

    private void validateUsername(UUID idFromToken, String username) {
        repository.findByUsername(username).ifPresent((stored) -> {
            repository.findById(idFromToken).ifPresent((user) -> {
                        if (!Objects.equals(stored, user)) {
                            throw new DataIntegrityViolationException("username");
                        }
                    }
            );
        });
    }

    private void validateActiveField(boolean active) {
        if (!active) throw new EntityNotFoundException();
    }

    private void validateBidirectionalFollowAccess(@Nullable User requesting, User requested) {
        if (!requested.isProfilePrivate()) return;
        if (requesting == null) throw new AccessDeniedException("Não há vínculo bidirecional entre os usuários.");
        boolean isSameUser = Objects.equals(requesting.getUsername(), requested.getUsername());
        boolean requestedContainsRequesting = requested.getFollowing().contains(requesting);
        boolean requestingContainsRequested = requesting.getFollowing().contains(requested);
        if (!isSameUser && (!requestedContainsRequesting || !requestingContainsRequested)) {
            throw new AccessDeniedException("Não há vínculo bidirecional entre os usuários.");
        }
    }

    private Page<User> getUserConnections(Pageable pageable, String q, UUID userRequestingId, String userRequestedUsername, Function<User, Set<User>> getter) {
        User requesting = (userRequestingId != null) ? repository.findById(userRequestingId).orElseThrow(EntityNotFoundException::new) : null;
        User requested = repository.findByUsername(userRequestedUsername).orElseThrow(EntityNotFoundException::new);
        validateBidirectionalFollowAccess(requesting, requested);
        List<UUID> ids = getter.apply(requested).stream().map(User::getId).toList();
        return repository.findAllByIdIn(pageable, q, ids);
    }

    private boolean isFollowing(User follower, User following) {
        if (Objects.equals(follower.getId(), following.getId())) {
            throw new CustomExceptions.SelfFollowException();
        }
        return following.getFollowers().contains(follower);
    }

    @Override
    public User create(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent() && repository.findByUsername(user.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("both");
        }
        validateEmail(null, user.getEmail());
        validateUsername(null, user.getUsername());
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public String generateActivationToken(User user) {
        return tokenService.generateActivationToken(user);
    }

    @Override
    public void activate(UUID idFromToken) {
        changeField(idFromToken, "active", User::isActive, user -> user.setActive(true));
    }

    @Override
    public User edit(UUID idFromToken, User user) {
        validateUsername(idFromToken, user.getUsername());
        changeField(idFromToken, "username", User::getUsername, stored -> stored.setUsername(user.getUsername()));
        changeField(idFromToken, "display_name", User::getDisplayName, stored -> stored.setDisplayName(user.getDisplayName()));
        changeField(idFromToken, "avatar", User::getAvatar, stored -> stored.setAvatar(user.getAvatar()));
        changeField(idFromToken, "banner", User::getBanner, stored -> stored.setBanner(user.getBanner()));
        changeField(idFromToken, "message", User::getMessage, stored -> stored.setMessage(user.getMessage()));
        changeField(idFromToken, "profile_private", User::isProfilePrivate, stored -> stored.setProfilePrivate(user.isProfilePrivate()));
        return user;
    }

    @Override
    public void changeProfileVisibility(UUID idFromToken) {
        changeField(idFromToken, "profile_private", User::isProfilePrivate, user -> user.setProfilePrivate(!user.isProfilePrivate()));
    }

    @Override
    public void changeEmail(UUID idFromToken, String email) {
        validateEmail(idFromToken, email);
        changeField(idFromToken, "email", User::getEmail, user -> user.setEmail(email.toLowerCase()));
    }

    @Override
    public void changeUsername(UUID idFromToken, String username) {
        validateUsername(idFromToken, username);
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
    public void follow(UUID idFromToken, String username) {
        User follower = repository.findByIdWithFollowersAndFollowing(idFromToken).orElseThrow(EntityNotFoundException::new);
        User following = repository.findByUsernameWithFollowersAndFollowing(username).orElseThrow(EntityNotFoundException::new);
        if (isFollowing(follower, following)) throw new CustomExceptions.AlreadyFollowingException();
        counter.updateFollowersAndFollowingCount(follower, following, true);
        notifier.notify(following, follower, MessageNotification.of(follower));
    }

    @Override
    public void unfollow(UUID idFromToken, String username) {
        User follower = repository.findByIdWithFollowersAndFollowing(idFromToken).orElseThrow(EntityNotFoundException::new);
        User following = repository.findByUsernameWithFollowersAndFollowing(username).orElseThrow(EntityNotFoundException::new);
        if (!isFollowing(follower, following)) throw new CustomExceptions.NotFollowingException();
        counter.updateFollowersAndFollowingCount(follower, following, false);
    }

    @Override
    public void delete(UUID idFromToken) {
        User user = repository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        user.getFollowing().forEach(following -> {
            following.getFollowers().remove(user);
            following.setFollowersCount(following.getFollowersCount() - 1);
            repository.save(following);
        });
        user.getFollowers().forEach(follower -> {
            follower.getFollowing().remove(user);
            follower.setFollowingCount(follower.getFollowingCount() - 1);
            repository.save(follower);
        });
        repository.delete(user);
    }

    @Override
    public Page<User> getAllActiveUsers(Pageable pageable) {
        return repository.findAllByActiveTrue(pageable);
    }

    @Override
    public Page<User> findUser(Pageable pageable, String q) {
        return repository.findAllActiveUsersByUsernameOrDisplayName(pageable, q);
    }

    @Override
    public User getUser(String username) {
        User user = repository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        validateActiveField(user.isActive());
        return user;
    }

    @Override
    public Page<User> getUserFollowing(Pageable pageable, String q, UUID idFromToken, String username) {
        return getUserConnections(pageable, q, idFromToken, username, User::getFollowing);
    }

    @Override
    public Page<User> getUserFollowers(Pageable pageable, String q, UUID idFromToken, String username) {
        return getUserConnections(pageable, q, idFromToken, username, User::getFollowers);
    }

    @Override
    public Set<String> getUserMutualConnections(UUID id) {
        User user = repository.findByIdWithFollowersAndFollowing(id).orElseThrow(EntityNotFoundException::new);
        Set<String> following = user.getFollowing().stream().map(User::getUsername).collect(Collectors.toSet());
        Set<String> followers = user.getFollowers().stream().map(User::getUsername).collect(Collectors.toSet());
        following.retainAll(followers);
        return following;
    }

    @Override
    public List<String> getUserDisplayNameHistory(String username) {
        User user = repository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        validateActiveField(user.isActive());
        return historian.getLastFiveUserDisplayName(user);
    }

    @Override
    public void cleanUsersWithExpiredActivationTime() {
        Instant nowMinus7Days = Instant.now().minus(7, ChronoUnit.DAYS);
        List<User> usersWithExpiredActivationTime = repository.findUsersWithExpiredActivationTime(nowMinus7Days);
        repository.deleteAll(usersWithExpiredActivationTime);
    }

}