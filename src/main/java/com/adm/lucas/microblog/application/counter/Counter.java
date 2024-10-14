package com.adm.lucas.microblog.application.counter;

import com.adm.lucas.microblog.domain.comment.Comment;
import com.adm.lucas.microblog.domain.comment.CommentRepository;
import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.note.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Counter {

    private final NoteRepository noteRepository;
    private final CommentRepository commentRepository;

    public void updateCommentsCount(Note note, boolean increment) {
        int commentsCount = note.getCommentsCount();
        if (increment) {
            note.setCommentsCount(commentsCount + 1);
        } else {
            note.setCommentsCount(commentsCount - 1);
        }
        noteRepository.save(note);
    }

    public void updateFlamesCount(Note note, boolean increment) {
        int flamesCount = note.getFlamesCount();
        if (increment) {
            note.setFlamesCount(flamesCount + 1);
        } else {
            note.setFlamesCount(flamesCount - 1);
        }
        noteRepository.save(note);
    }

    public void updateRepliesCount(Comment comment, boolean increment) {
        int repliesCount = comment.getRepliesCount();
        if (increment) {
            comment.setRepliesCount(repliesCount + 1);
        } else {
            comment.setRepliesCount(repliesCount - 1);
        }
        commentRepository.save(comment);
    }

}