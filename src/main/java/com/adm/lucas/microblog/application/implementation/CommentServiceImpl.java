package com.adm.lucas.microblog.application.implementation;

import com.adm.lucas.microblog.application.dto.request.comment.CreateCommentREQ;
import com.adm.lucas.microblog.domain.comment.Comment;
import com.adm.lucas.microblog.domain.comment.CommentRepository;
import com.adm.lucas.microblog.domain.comment.CommentService;
import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.note.NoteRepository;
import com.adm.lucas.microblog.domain.user.User;
import com.adm.lucas.microblog.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final CommentRepository repository;

    private void validateAccess(UUID idFromToken, Comment comment) {
        if (!Objects.equals(idFromToken, comment.getUser().getId())) {
            throw new AccessDeniedException("Usuário sem permissão.");
        }
    }

    @Override
    public Comment mapToComment(UUID idFromToken, UUID noteIdFromPath, CreateCommentREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        Note note = noteRepository.findById(noteIdFromPath).orElseThrow(EntityNotFoundException::new);
        return new Comment(user, note, req.text());
    }

    @Override
    public Comment create(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public void edit(UUID idFromToken, UUID idFromPath, String text) {
        Comment comment = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, comment);
        comment.setText(text);
        comment.setModified(true);
        comment.setModifiedAt(Instant.now());
        repository.save(comment);
    }

    @Override
    public void delete(UUID idFromToken, UUID idFromPath) {
        Comment comment = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, comment);
        repository.delete(comment);
    }

    @Override
    public Page<Comment> getComments(Pageable pageable, UUID noteIdFromPath) {
        return repository.findAllByNoteId(pageable, noteIdFromPath);
    }

}