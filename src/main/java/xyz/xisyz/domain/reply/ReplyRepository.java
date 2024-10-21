package xyz.xisyz.domain.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, UUID> {

    @EntityGraph(attributePaths = {"user"})
    Page<Reply> findAllByCommentId(Pageable pageable, UUID idFromPath);

}