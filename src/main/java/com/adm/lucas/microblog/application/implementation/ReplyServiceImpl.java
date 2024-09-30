package com.adm.lucas.microblog.application.implementation;

import com.adm.lucas.microblog.application.dto.notification.MessageNotification;
import com.adm.lucas.microblog.application.dto.request.reply.CreateReplyREQ;
import com.adm.lucas.microblog.domain.comment.Comment;
import com.adm.lucas.microblog.domain.comment.CommentRepository;
import com.adm.lucas.microblog.domain.notification.NotificationService;
import com.adm.lucas.microblog.domain.reply.Reply;
import com.adm.lucas.microblog.domain.reply.ReplyRepository;
import com.adm.lucas.microblog.domain.reply.ReplyService;
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
public class ReplyServiceImpl implements ReplyService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository repository;
    private final NotificationService notifier;

    private void validateAccess(UUID idFromToken, Reply reply) {
        if (!Objects.equals(idFromToken, reply.getUser().getId())) {
            throw new AccessDeniedException("Usuário sem permissão.");
        }
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
        repository.save(reply);
        if (reply.getToUser() == null) notifier.notify(reply.getComment().getUser(), reply.getUser(), MessageNotification.of(reply));
        if (reply.getToUser() != null) notifier.notify(reply.getToReply().getUser(), reply.getUser(), MessageNotification.of(reply));
        return reply;
    }

    @Override
    public void edit(UUID idFromToken, UUID idFromPath, String text) {
        Reply reply = repository.findById(idFromPath).orElseThrow(EntityNotFoundException::new);
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
        repository.delete(reply);
    }

    @Override
    public Page<Reply> getReplies(Pageable pageable, UUID commentIdFromPath) {
        return repository.findAllByCommentId(pageable, commentIdFromPath);
    }

}