package br.com.notehub.domain.reply;

import br.com.notehub.application.dto.request.reply.CreateReplyREQ;
import br.com.notehub.application.dto.response.page.PageRES;
import br.com.notehub.application.dto.response.reply.CreateReplyRES;
import br.com.notehub.application.dto.response.reply.DetailReplyRES;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ReplyService {

    CreateReplyRES create(UUID idFromToken, UUID commentIdFromPath, boolean toReply, CreateReplyREQ req);

    void edit(UUID idFromToken, UUID idFromPath, String text);

    void delete(UUID idFromToken, UUID idFromPath);

    PageRES<DetailReplyRES> getReplies(Pageable pageable, UUID idFromToken, UUID commentIdFromPath);

}