package xyz.xisyz.domain.flame;

import xyz.xisyz.domain.note.Note;
import xyz.xisyz.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlameRepository extends JpaRepository<Flame, UUID> {

    Optional<Flame> findByUserIdAndNoteId(UUID userId, UUID noteId);

    boolean existsByUserAndNote(User user, Note note);

    List<Flame> findAllByUserId(UUID id);

}