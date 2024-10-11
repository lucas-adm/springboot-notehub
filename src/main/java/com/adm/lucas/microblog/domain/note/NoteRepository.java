package com.adm.lucas.microblog.domain.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.user LEFT JOIN FETCH n.tags WHERE n.id = :id")
    Optional<Note> findByIdWithUserAndTags(@Param("id") UUID id);

    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.user LEFT JOIN FETCH n.tags WHERE n.id = :id AND n.hidden = false")
    Optional<Note> findByIdAndHiddenFalseWithUserAndTags(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"user", "tags"})
    Optional<Note> findByIdAndHiddenFalseAndUserProfilePrivateFalse(UUID id);

    @Query("""
            SELECT DISTINCT n FROM Note n
            LEFT JOIN FETCH n.user u
            LEFT JOIN FETCH n.tags t
            WHERE u.profilePrivate = false
            AND n.hidden = false
            AND (
                LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR
                EXISTS (
                    SELECT 1 FROM Tag tag
                    JOIN tag.notes note
                    WHERE note.id = n.id AND LOWER(tag.name) LIKE LOWER(CONCAT('%', :q, '%'))
                )
            )
            """)
    Page<Note> searchPublicNotesByTitleOrTag(Pageable pageable, @Param("q") String q);

    @Query("""
            SELECT DISTINCT n FROM Note n
            LEFT JOIN FETCH n.user u
            LEFT JOIN FETCH n.tags t
            WHERE u.profilePrivate = false
            AND n.hidden = false
            AND (
                EXISTS (
                    SELECT 1 FROM Tag tag
                    JOIN tag.notes note
                    WHERE note.id = n.id AND LOWER(tag.name) LIKE LOWER(CONCAT('%', :q, '%'))
                )
            )
            """)
    Page<Note> searchPublicNotesByTag(Pageable pageable, String q);

    @Query("""
            SELECT DISTINCT n FROM Note n
            LEFT JOIN FETCH n.user u
            LEFT JOIN FETCH n.tags t
            WHERE u.id = :id
            AND (
                LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR
                EXISTS (
                    SELECT 1 FROM Tag tag
                    JOIN tag.notes note
                    WHERE note.id = n.id AND LOWER(tag.name) LIKE LOWER(CONCAT('%', :q, '%'))
                )
            )
            """)
    Page<Note> searchPrivateNotesByTitleOrTag(Pageable pageable, @Param("id") UUID id, @Param("q") String q);

    @Query("""
            SELECT DISTINCT n FROM Note n
            LEFT JOIN FETCH n.user u
            LEFT JOIN FETCH n.tags t
            WHERE
            u.id = :id
            AND
            EXISTS (
                SELECT 1 FROM Tag tag
                JOIN tag.notes note
                WHERE note.id = n.id AND LOWER(tag.name) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            """)
    Page<Note> searchPrivateNotesByTag(Pageable pageable, UUID id, String q);

    @EntityGraph(attributePaths = {"user", "tags"})
    Page<Note> findAllByUserId(Pageable pageable, UUID id);

    @EntityGraph(attributePaths = {"user", "tags"})
    Page<Note> findAllByUserProfilePrivateFalseAndUserUsernameAndHiddenFalse(Pageable pageable, String username);

    @EntityGraph(attributePaths = {"user", "tags"})
    Page<Note> findAllByHiddenFalseAndUserProfilePrivateFalseAndUserIdIn(Pageable pageable, List<UUID> following);

    @EntityGraph(attributePaths = {"user", "tags"})
    Page<Note> findAllByUserUsernameAndHiddenFalse(Pageable pageable, String username);

}