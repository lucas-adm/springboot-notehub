package com.adm.lucas.microblog.domain.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCounterService {

    private final CommentRepository repository;

    public void updateRepliesCount(Comment comment, boolean increment) {
        int repliesCount = comment.getRepliesCount();
        if (increment) {
            comment.setRepliesCount(repliesCount + 1);
        } else {
            comment.setRepliesCount(repliesCount - 1);
        }
        repository.save(comment);
    }

}