package xyz.xisyz.domain.reply;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.xisyz.application.dto.request.reply.CreateReplyREQ;
import xyz.xisyz.application.dto.response.page.PageRES;
import xyz.xisyz.application.dto.response.reply.CreateReplyRES;
import xyz.xisyz.application.dto.response.reply.DetailReplyRES;

import java.util.UUID;

@Service
public interface ReplyService {

    CreateReplyRES create(UUID idFromToken, UUID commentIdFromPath, boolean toReply, CreateReplyREQ req);

    void edit(UUID idFromToken, UUID idFromPath, String text);

    void delete(UUID idFromToken, UUID idFromPath);

    PageRES<DetailReplyRES> getReplies(Pageable pageable, UUID idFromToken, UUID commentIdFromPath);

}