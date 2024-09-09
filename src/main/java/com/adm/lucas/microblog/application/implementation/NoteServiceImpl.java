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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final NoteRepository repository;

    @Override
    public Note map(UUID idFromToken, CreateNoteREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        List<Tag> tags = req.tags().stream().map(tag -> tagRepository.findByName(tag).orElseGet(() -> new Tag(tag))).toList();
        return new Note(user, req.title(), req.markdown(), req.closed(), req.hidden(), tags);
    }

    @Override
    public Note create(Note note) {
        return repository.save(note);
    }

}