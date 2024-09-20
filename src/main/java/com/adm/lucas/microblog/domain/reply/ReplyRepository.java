package com.adm.lucas.microblog.domain.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, UUID> {

    Page<Reply> findAllByCommentId(Pageable pageable, UUID idFromPath);

}