package com.adm.lucas.microblog.domain.flame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlameRepository extends JpaRepository<Flame, UUID> {

    Optional<Flame> findByNoteId(UUID id);

    List<Flame> findAllByUserId(UUID id);

}