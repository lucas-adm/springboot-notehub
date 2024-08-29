package com.adm.lucas.microblog.domain.repository;

import com.adm.lucas.microblog.domain.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, UUID> {
}