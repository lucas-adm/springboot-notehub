package com.adm.lucas.microblog.domain.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    Page<Note> findAllByTitleContainingIgnoreCaseAndHiddenFalseOrTagsNameContainingIgnoreCaseAndHiddenFalse(Pageable pageable, String title, String name);

    Page<Note> findAllByTagsNameContainingIgnoreCaseAndHiddenFalse(Pageable pageable, String name);

    Optional<Note> findByIdAndHiddenFalse(UUID id);

    Page<Note> findAllByUserId(Pageable pageable, UUID id);

    Page<Note> findAllByUserIdAndTitleContainingIgnoreCaseOrUserIdAndTagsNameContainingIgnoreCase(Pageable pageable, UUID id, String title, UUID userId, String name);

    Page<Note> findAllByUserIdAndTagsNameContainingIgnoreCase(Pageable pageable, UUID id, String name);

}