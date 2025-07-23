package br.com.notehub.domain.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findByName(String name);

    List<Tag> findAllByNameIn(List<String> names);

    List<Tag> findAllByNotesUserId(UUID id);

    List<Tag> findAllByNotesUserIdAndNotesHiddenTrue(UUID id);

    List<Tag> findAllByNotesUserUsernameAndNotesHiddenFalseOrderByNameAsc(String username);

}