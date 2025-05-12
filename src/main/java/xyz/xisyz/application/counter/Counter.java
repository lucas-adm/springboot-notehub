package xyz.xisyz.application.counter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.xisyz.domain.comment.Comment;
import xyz.xisyz.domain.comment.CommentRepository;
import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.note.NoteRepository;
import xyz.xisyz.domain.user.User;
import xyz.xisyz.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
public class Counter {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final CommentRepository commentRepository;

    public void updateNotesCount(User user, boolean increment) {
        int notesCount = user.getNotesCount();
        if (increment) {
            user.setNotesCount(notesCount + 1);
        } else {
            user.setNotesCount(notesCount - 1);
        }
        userRepository.save(user);
    }

    public void updateFollowersAndFollowingCount(User follower, User following, boolean increment) {
        if (increment) {
            follower.getFollowing().add(following);
            follower.setFollowingCount(follower.getFollowingCount() + 1);
            following.getFollowers().add(follower);
            following.setFollowersCount(following.getFollowersCount() + 1);
        } else {
            follower.getFollowing().remove(following);
            follower.setFollowingCount(follower.getFollowingCount() - 1);
            following.getFollowers().remove(follower);
            following.setFollowersCount(following.getFollowersCount() - 1);
        }
        userRepository.save(follower);
        userRepository.save(following);
    }

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