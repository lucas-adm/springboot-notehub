package xyz.xisyz.application.implementation.comment;

import xyz.xisyz.application.counter.Counter;
import xyz.xisyz.application.dto.notification.MessageNotification;
import xyz.xisyz.application.dto.request.comment.CreateCommentREQ;
import xyz.xisyz.domain.comment.Comment;
import xyz.xisyz.domain.comment.CommentRepository;
import xyz.xisyz.domain.comment.CommentService;
import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.note.NoteRepository;
import xyz.xisyz.domain.notification.NotificationService;
import xyz.xisyz.domain.user.User;
import xyz.xisyz.domain.user.UserRepository;
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
    private final NotificationService notifier;
    private final Counter counter;

    private void validateAccess(UUID idFromToken, Comment comment) {
        if (!Objects.equals(idFromToken, comment.getUser().getId())) {
            throw new AccessDeniedException("Usuário sem permissão.");
        }
    }

    private void assertNoteIsNotClosed(Comment comment) {
        if (comment.getNote().isClosed()) throw new IllegalStateException("Nota fechada para novas interações.");
    }

    @Override
    public Comment mapToComment(UUID idFromToken, UUID noteIdFromPath, CreateCommentREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        Note note = noteRepository.findById(noteIdFromPath).orElseThrow(EntityNotFoundException::new);
        return new Comment(user, note, req.text());
    }

    @Override
    public Comment create(Comment comment) {
        assertNoteIsNotClosed(comment);
        repository.save(comment);
        counter.updateCommentsCount(comment.getNote(), true);
        notifier.notify(comment.getNote().getUser(), comment.getUser(), MessageNotification.of(comment));
        return comment;
    }

    @Override
    public void edit(UUID idFromToken, UUID idFromPath, String text) {
        Comment comment = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, comment);
        assertNoteIsNotClosed(comment);
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
        counter.updateCommentsCount(comment.getNote(), false);
    }

    @Override
    public Page<Comment> getComments(Pageable pageable, UUID noteIdFromPath) {
        return repository.findAllByNoteId(pageable, noteIdFromPath);
    }

}