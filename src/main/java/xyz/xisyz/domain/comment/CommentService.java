package xyz.xisyz.domain.comment;

import xyz.xisyz.application.dto.request.comment.CreateCommentREQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface CommentService {

    Comment mapToComment(UUID idFromToken, UUID noteIdFromPath, CreateCommentREQ req);

    Comment create(Comment comment);

    void edit(UUID idFromToken, UUID idFromPath, String text);

    void delete(UUID idFromToken, UUID idFromPath);

    Page<Comment> getComments(Pageable pageable, UUID noteIdFromPath);

}