package xyz.xisyz.domain.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.xisyz.application.dto.request.comment.CreateCommentREQ;
import xyz.xisyz.application.dto.response.comment.CreateCommentRES;
import xyz.xisyz.application.dto.response.comment.DetailCommentRES;
import xyz.xisyz.application.dto.response.page.PageRES;

import java.util.UUID;

@Service
public interface CommentService {

    CreateCommentRES create(UUID idFromToken, UUID noteIdFromPath, CreateCommentREQ req);

    void edit(UUID idFromToken, UUID idFromPath, String text);

    void delete(UUID idFromToken, UUID idFromPath);

    PageRES<DetailCommentRES> getComments(Pageable pageable, UUID idFromToken, UUID noteIdFromPath);

}