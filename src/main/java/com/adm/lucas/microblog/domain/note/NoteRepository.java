package com.adm.lucas.microblog.domain.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("""
            SELECT n FROM Note n
            LEFT JOIN n.tags t
            WHERE (
                LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR
                LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            AND n.hidden = false
            """
    )
    Page<Note> findAllNotHiddenByTitleOrTagName(Pageable pageable, @Param("q") String q);

    Page<Note> findAllByHiddenFalseAndTagsNameContainingIgnoreCase(Pageable pageable, String q);

    Optional<Note> findByHiddenFalseAndId(UUID id);

    Page<Note> findAllByUserId(Pageable pageable, UUID id);

    @Query("""
            SELECT n FROM Note n
            LEFT JOIN n.user u
            LEFT JOIN n.tags t
            WHERE u.id = :id
            AND (
                LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR
                LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            """)
    Page<Note> findAllCurrentUserNotesByTitleOrTagName(Pageable pageable, UUID id, @Param("q") String q);

    Page<Note> findAllByUserIdAndTagsNameContainingIgnoreCase(Pageable pageable, UUID id, String q);

    Page<Note> findAllByUserProfilePrivateFalseAndUserUsername(Pageable pageable, String username);

}