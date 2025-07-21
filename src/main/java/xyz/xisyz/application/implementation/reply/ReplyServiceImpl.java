package xyz.xisyz.application.implementation.reply;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.xisyz.application.counter.Counter;
import xyz.xisyz.application.dto.notification.MessageNotification;
import xyz.xisyz.application.dto.request.reply.CreateReplyREQ;
import xyz.xisyz.application.dto.response.page.PageRES;
import xyz.xisyz.application.dto.response.reply.CreateReplyRES;
import xyz.xisyz.application.dto.response.reply.DetailReplyRES;
import xyz.xisyz.domain.comment.Comment;
import xyz.xisyz.domain.comment.CommentRepository;
import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.notification.NotificationService;
import xyz.xisyz.domain.reply.Reply;
import xyz.xisyz.domain.reply.ReplyRepository;
import xyz.xisyz.domain.reply.ReplyService;
import xyz.xisyz.domain.user.User;
import xyz.xisyz.domain.user.UserRepository;

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

    private void validateAccess(@Nullable UUID idFromToken, UUID idFromRequested) {
        if (idFromToken == null) throw new AccessDeniedException("Usuário sem permissão.");
        if (!Objects.equals(idFromToken, idFromRequested)) {
            throw new AccessDeniedException("Usuário sem permissão.");
        }
    }

    private void validateBidirectionalFollowAccess(@Nullable User requesting, User requested) {
        if (requesting == null) throw new AccessDeniedException("Não há vínculo bidirecional entre os usuários.");
        boolean isSameUser = Objects.equals(requesting.getUsername(), requested.getUsername());
        boolean requestedContainsRequesting = requested.getFollowing().contains(requesting);
        boolean requestingContainsRequested = requesting.getFollowing().contains(requested);
        if (!isSameUser && (!requestedContainsRequesting || !requestingContainsRequested)) {
            throw new AccessDeniedException("Não há vínculo bidirecional entre os usuários.");
        }
    }

    private void assertNoteIsNotClosed(Reply reply) {
        if (reply.getComment().getNote().isClosed()) throw new IllegalStateException("Nota fechada para novas interações.");
    }

    public Reply mapToReply(UUID idFromToken, UUID commentIdFromPath, CreateReplyREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        Comment comment = commentRepository.findById(commentIdFromPath).orElseThrow(EntityNotFoundException::new);
        return new Reply(user, comment, req.text());
    }

    public Reply mapToSelfReference(UUID idFromToken, UUID replyIdFromPath, CreateReplyREQ req) {
        User user = userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new);
        Reply reply = repository.findById(replyIdFromPath).orElseThrow(EntityNotFoundException::new);
        Comment comment = reply.getComment();
        return new Reply(user, comment, reply, req.text());
    }

    @Transactional
    @Override
    public CreateReplyRES create(UUID idFromToken, UUID idFromPath, boolean toReply, CreateReplyREQ req) {
        Reply reply = toReply ? mapToSelfReference(idFromToken, idFromPath, req) : mapToReply(idFromToken, idFromPath, req);
        assertNoteIsNotClosed(reply);
        repository.save(reply);
        counter.updateRepliesCount(reply.getComment(), true);
        if (reply.getToUser() == null) notifier.notify(
                reply.getUser(),
                reply.getComment().getUser(),
                reply.getComment().getUser(),
                MessageNotification.of(reply));
        if (reply.getToUser() != null) notifier.notify(
                reply.getUser(),
                reply.getToReply().getUser(),
                reply.getComment().getUser(),
                MessageNotification.of(reply));
        return new CreateReplyRES(reply);
    }

    @Transactional
    @Override
    public void edit(UUID idFromToken, UUID idFromPath, String text) {
        Reply reply = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        assertNoteIsNotClosed(reply);
        validateAccess(idFromToken, reply.getUser().getId());
        reply.setText(text);
        reply.setModifiedAt(Instant.now());
        reply.setModified(true);
        repository.save(reply);
    }

    @Transactional
    @Override
    public void delete(UUID idFromToken, UUID idFromPath) {
        Reply reply = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
        validateAccess(idFromToken, reply.getUser().getId());
        counter.updateRepliesCount(reply.getComment(), false);
        repository.delete(reply);
    }

    @Transactional(readOnly = true)
    @Override
    public PageRES<DetailReplyRES> getReplies(Pageable pageable, UUID idFromToken, UUID commentIdFromPath) {
        User requesting = (idFromToken != null) ? userRepository.findById(idFromToken).orElseThrow(EntityNotFoundException::new) : null;
        Note requested = commentRepository.findById(commentIdFromPath).orElseThrow(EntityNotFoundException::new).getNote();
        User author = requested.getUser();
        if (author != null) {
            if (requested.isHidden()) validateAccess(idFromToken, author.getId());
            if (author.isProfilePrivate()) validateBidirectionalFollowAccess(requesting, author);
        }
        Page<DetailReplyRES> page = repository.findAllByCommentId(pageable, commentIdFromPath).map(DetailReplyRES::new);
        return new PageRES<>(page);
    }

}