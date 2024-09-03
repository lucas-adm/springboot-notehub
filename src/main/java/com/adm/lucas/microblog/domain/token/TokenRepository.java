package com.adm.lucas.microblog.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByUserId(UUID id);

    Optional<Token> findByAccessToken(String accessToken);

}