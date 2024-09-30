package com.adm.lucas.microblog.application.implementation;

import com.adm.lucas.microblog.application.dto.notification.MessageNotification;
import com.adm.lucas.microblog.domain.flame.Flame;
import com.adm.lucas.microblog.domain.flame.FlameRepository;
import com.adm.lucas.microblog.domain.flame.FlameService;
import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.note.NoteRepository;
import com.adm.lucas.microblog.domain.notification.NotificationService;
import com.adm.lucas.microblog.domain.user.User;
import com.adm.lucas.microblog.domain.user.UserRepository;
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

    @Override
    public void inflame(UUID userIdFromToken, UUID noteIdFromPath) {
        User user = userRepository.findById(userIdFromToken).orElseThrow(EntityNotFoundException::new);
        Note note = noteRepository.findById(noteIdFromPath).orElseThrow(EntityNotFoundException::new);
        Flame flame = repository.save(new Flame(user, note));
        notifier.notify(note.getUser(), user, MessageNotification.of(flame));
    }

    @Override
    public void deflame(UUID noteIdFromPath) {
        Flame flame = repository.findByNoteId(noteIdFromPath).orElseThrow(EntityNotFoundException::new);
        repository.delete(flame);
    }

    @Override
    public List<UUID> getUserInflamedNotes(UUID userIdFromToken) {
        List<Flame> flames = repository.findAllByUserId(userIdFromToken);
        return flames.stream().map(flame -> flame.getNote().getId()).toList();
    }

}