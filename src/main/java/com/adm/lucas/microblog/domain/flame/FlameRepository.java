package com.adm.lucas.microblog.domain.flame;

import com.adm.lucas.microblog.domain.note.Note;
import com.adm.lucas.microblog.domain.user.User;
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