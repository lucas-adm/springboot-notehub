package xyz.xisyz.application.implementation.reply;

import xyz.xisyz.application.counter.Counter;
import xyz.xisyz.application.dto.notification.MessageNotification;
import xyz.xisyz.application.dto.request.reply.CreateReplyREQ;
import xyz.xisyz.domain.comment.Comment;
import xyz.xisyz.domain.comment.CommentRepository;
import xyz.xisyz.domain.notification.NotificationService;
import xyz.xisyz.domain.reply.Reply;
import xyz.xisyz.domain.reply.ReplyRepository;
import xyz.xisyz.domain.reply.ReplyService;
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
public class ReplyServiceImpl implements ReplyService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository repository;
    private final NotificationService notifier;
    private final Counter counter;

    private void validateAccess(UUID idFromToken, Reply reply) {
        if (!Objects.equals(idFromToken, reply.getUser().getId())) {
            throw new AccessDeniedException("Usuário sem permissão.");
        }
    }

    private void assertNoteIsNotClosed(Reply reply) {
        if (reply.getComment().getNote().isClosed()) throw new IllegalStateException("Nota fechada para novas interações.");
    }

    @Override
    public Reply mapToReply(UUID idFromToken, UUID commentIdFromPath, CreateReplyREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        Comment comment = commentRepository.findById(commentIdFromPath).orElseThrow(EntityNotFoundException::new);
        return new Reply(user, comment, req.text());
    }

    @Override
    public Reply mapToSelfReference(UUID idFromToken, UUID replyIdFromPath, CreateReplyREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        Reply reply = repository.findById(replyIdFromPath).orElseThrow(EntityNotFoundException::new);
        Comment comment = reply.getComment();
        return new Reply(user, comment, reply, req.text());
    }

    @Override
    public Reply create(Reply reply) {
        assertNoteIsNotClosed(reply);
        repository.save(reply);
        counter.updateRepliesCount(reply.getComment(), true);
        if (reply.getToUser() == null) notifier.notify(reply.getComment().getUser(), reply.getUser(), MessageNotification.of(reply));
        if (reply.getToUser() != null) notifier.notify(reply.getToReply().getUser(), reply.getUser(), MessageNotification.of(reply));
        return reply;
    }

    @Override
    public void edit(UUID idFromToken, UUID idFromPath, String text) {
        Reply reply = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        assertNoteIsNotClosed(reply);
        validateAccess(idFromToken, reply);
        reply.setText(text);
        reply.setModifiedAt(Instant.now());
        reply.setModified(true);
        repository.save(reply);
    }

    @Override
    public void delete(UUID idFromToken, UUID idFromPath) {
        Reply reply = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, reply);
        counter.updateRepliesCount(reply.getComment(), false);
        repository.delete(reply);
    }

    @Override
    public Page<Reply> getReplies(Pageable pageable, UUID commentIdFromPath) {
        return repository.findAllByCommentId(pageable, commentIdFromPath);
    }

}