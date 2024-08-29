package com.adm.lucas.microblog.repository;

import com.adm.lucas.microblog.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByUserId(UUID id);

    Optional<Token> findByAccessToken(String accessToken);

}