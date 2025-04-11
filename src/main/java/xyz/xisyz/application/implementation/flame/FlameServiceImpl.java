package xyz.xisyz.application.implementation.flame;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.xisyz.application.counter.Counter;
import xyz.xisyz.application.dto.notification.MessageNotification;
import xyz.xisyz.domain.flame.Flame;
import xyz.xisyz.domain.flame.FlameRepository;
import xyz.xisyz.domain.flame.FlameService;
import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.note.NoteRepository;
import xyz.xisyz.domain.notification.NotificationService;
import xyz.xisyz.domain.user.User;
import xyz.xisyz.domain.user.UserRepository;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FlameServiceImpl implements FlameService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final FlameRepository repository;
    private final NotificationService notifier;
    private final Counter counter;

    private void validateBidirectionalFollowAccess(User requesting, User requested) {
        if (!Objects.equals(requesting.getUsername(), requested.getUsername())
                && !requested.getFollowing().contains(requesting)
                && !requesting.getFollowers().contains(requested)
        ) {
            throw new AccessDeniedException("Não há vínculo bidirecional entre os usuários.");
        }
    }

    @Override
    public void inflame(UUID userIdFromToken, UUID noteIdFromPath) {
        User user = userRepository.findById(userIdFromToken).orElseThrow(EntityNotFoundException::new);
        Note note = noteRepository.findById(noteIdFromPath).orElseThrow(EntityNotFoundException::new);
        if (repository.existsByUserAndNote(user, note)) throw new EntityExistsException();
        Flame flame = repository.save(new Flame(user, note));
        counter.updateFlamesCount(note, true);
        notifier.notify(note.getUser(), user, MessageNotification.of(flame));
    }

    @Override
    public void deflame(UUID userIdFromToken, UUID noteIdFromPath) {
        Flame flame = repository.findByUserIdAndNoteId(userIdFromToken, noteIdFromPath).orElseThrow(EntityNotFoundException::new);
        counter.updateFlamesCount(flame.getNote(), false);
        repository.delete(flame);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Flame> getUserFlames(UUID userIdFromToken, Pageable pageable, String username, String q) {
        User requesting = userRepository.findById(userIdFromToken).orElseThrow(EntityNotFoundException::new);
        User requested = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        if (requested.isProfilePrivate()) validateBidirectionalFollowAccess(requesting, requested);
        return repository.getUserFlames(pageable, username, q);
    }

}