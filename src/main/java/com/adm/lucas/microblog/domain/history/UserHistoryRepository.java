package com.adm.lucas.microblog.domain.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, UUID> {
}