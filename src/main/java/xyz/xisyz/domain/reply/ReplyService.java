package xyz.xisyz.domain.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.xisyz.application.dto.request.reply.CreateReplyREQ;

import java.util.UUID;

@Service
public interface ReplyService {

    Reply mapToReply(UUID idFromToken, UUID commentIdFromPath, CreateReplyREQ req);

    Reply mapToSelfReference(UUID idFromToken, UUID replyIdFromPath, CreateReplyREQ req);

    Reply create(Reply reply);

    void edit(UUID idFromToken, UUID idFromPath, String text);

    void delete(UUID idFromToken, UUID idFromPath);

    Page<Reply> getReplies(Pageable pageable, UUID idFromToken, UUID commentIdFromPath);

}