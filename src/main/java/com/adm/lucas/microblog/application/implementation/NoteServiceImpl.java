package com.adm.lucas.microblog.application.implementation;

import com.adm.lucas.microblog.application.dto.request.note.CreateNoteREQ;
import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.note.NoteRepository;
import com.adm.lucas.microblog.domain.note.NoteService;
import com.adm.lucas.microblog.domain.tag.Tag;
import com.adm.lucas.microblog.domain.tag.TagRepository;
import com.adm.lucas.microblog.domain.user.User;
import com.adm.lucas.microblog.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final NoteRepository repository;

    private void validateAccess(UUID userId, UUID noteId) {
        Note note = repository.findById(noteId).orElseThrow(EntityNotFoundException::new);
        if (!Objects.equals(userId, note.getUser().getId())) throw new AccessDeniedException("Usuário sem permissão.");
    }

    private void deleteNoteAndFlush(Note note) {
        repository.delete(note);
        repository.flush();
    }

    private void removeOrphanTags(List<String> names) {
        List<Tag> tags = tagRepository.findAllByNameIn(names);
        for (Tag tag : tags) {
            if (tag.getNotes().isEmpty()) tagRepository.delete(tag);
        }
    }

    @Override
    public Note mapToNote(UUID idFromToken, CreateNoteREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        List<Tag> tags = req.tags().stream().map(tag -> tagRepository.findByName(tag).orElseGet(() -> new Tag(tag.toLowerCase()))).toList();
        return new Note(user, req.title(), req.markdown(), req.closed(), req.hidden(), tags);
    }

    @Override
    public Note create(Note note) {
        return repository.save(note);
    }

    @Override
    public void changeTags(UUID idFromToken, UUID idFromPath, List<String> tags) {
        validateAccess(idFromToken, idFromPath);
        Note note = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        List<String> names = note.getTags().stream().map(Tag::getName).toList();
        note.setTags(tags.stream().map(tag -> tagRepository.findByName(tag).orElseGet(() -> new Tag(tag.toLowerCase()))).collect(Collectors.toCollection(ArrayList::new)));
        repository.saveAndFlush(note);
        removeOrphanTags(names);
    }

    @Override
    public void delete(UUID idFromToken, UUID idFromPath) {
        validateAccess(idFromToken, idFromPath);
        Note note = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        List<String> names = note.getTags().stream().map(Tag::getName).toList();
        deleteNoteAndFlush(note);
        removeOrphanTags(names);
    }

}