package com.hugo.humami.repository;

import com.hugo.humami.domain.guides.GuideAccessTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GuideAccessTokenRepository extends MongoRepository<GuideAccessTokenEntity, String> {
    Optional<GuideAccessTokenEntity> findByTokenHash(String tokenHash);
}
