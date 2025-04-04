package xyz.xisyz.domain.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.xisyz.application.dto.request.note.CreateNoteREQ;

import java.util.List;
import java.util.UUID;

@Service
public interface NoteService {

    Note mapToNote(UUID idFromToken, CreateNoteREQ req);

    Note create(Note note);

    void edit(UUID idFromToken, UUID idFromPath, String title, List<String> tags, boolean closed, boolean hidden);

    void changeTitle(UUID idFromToken, UUID idFromPath, String title);

    void changeMarkdown(UUID idFromToken, UUID idFromPath, String markdown);

    void changeClosed(UUID idFromToken, UUID idFromPath);

    void changeHidden(UUID idFromToken, UUID idFromPath);

    void changeTags(UUID idFromToken, UUID idFromPath, List<String> tags);

    void delete(UUID idFromToken, UUID idFromPath);

    List<String> getAllTags();

    List<String> getAllUserTags(UUID idFromToken);

    Page<Note> findPublicNotes(Pageable pageable, String q);

    Page<Note> findPrivateNotes(Pageable pageable, UUID idFromToken, String q);

    Page<Note> findPublicNotesByTag(Pageable pageable, String tag);

    Page<Note> findPrivateNotesByTag(Pageable pageable, UUID idFromToken, String tag);

    Page<Note> findUserNotesBySpecs(UUID idFromToken, Pageable pageable, String username, String q, String tag, String type);

    Note getPublicNote(UUID idFromPath);

    Note getPrivateNote(UUID idFromToken, UUID idFromPath);

    Note getPrivateFollowingUserNote(UUID idFromToken, UUID idFromPath);

    Page<Note> getAllUserNotesByUsername(Pageable pageable, String username);

    Page<Note> getAllUserNotesById(Pageable pageable, UUID idFromToken);

    Page<Note> getAllFollowedUsersNotes(Pageable pageable, UUID idFromToken);

    Page<Note> getAllFollowedUserNotes(Pageable pageable, UUID idFromToken, String username);

}