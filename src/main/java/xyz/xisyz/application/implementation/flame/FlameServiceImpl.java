package xyz.xisyz.application.implementation.flame;

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
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FlameServiceImpl implements FlameService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final FlameRepository repository;
    private final NotificationService notifier;
    private final Counter counter;

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

    @Override
    public List<UUID> getUserInflamedNotes(UUID userIdFromToken) {
        List<Flame> flames = repository.findAllByUserId(userIdFromToken);
        return flames.stream().map(flame -> flame.getNote().getId()).toList();
    }

}