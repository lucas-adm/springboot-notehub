package com.adm.lucas.microblog.domain.note;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteCounterService {

    private final NoteRepository repository;

    public void updateCommentsCount(Note note, boolean increment) {
        int commentsCount = note.getCommentsCount();
        if (increment) {
            note.setCommentsCount(commentsCount + 1);
        } else {
            note.setCommentsCount(commentsCount - 1);
        }
        repository.save(note);
    }

    public void updateFlamesCount(Note note, boolean increment) {
        int flamesCount = note.getFlamesCount();
        if (increment) {
            note.setFlamesCount(flamesCount + 1);
        } else {
            note.setFlamesCount(flamesCount - 1);
        }
        repository.save(note);
    }

}