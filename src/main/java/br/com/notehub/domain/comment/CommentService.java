package br.com.notehub.domain.comment;

import br.com.notehub.application.dto.request.comment.CreateCommentREQ;
import br.com.notehub.application.dto.response.comment.CreateCommentRES;
import br.com.notehub.application.dto.response.comment.DetailCommentRES;
import br.com.notehub.application.dto.response.page.PageRES;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface CommentService {

    CreateCommentRES create(UUID idFromToken, UUID noteIdFromPath, CreateCommentREQ req);

    void edit(UUID idFromToken, UUID idFromPath, String text);

    void delete(UUID idFromToken, UUID idFromPath);

    PageRES<DetailCommentRES> getComments(Pageable pageable, UUID idFromToken, UUID noteIdFromPath);

}