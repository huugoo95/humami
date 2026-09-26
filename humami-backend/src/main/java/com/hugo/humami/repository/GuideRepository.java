package com.hugo.humami.repository;

import com.hugo.humami.domain.guides.GuideEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GuideRepository extends MongoRepository<GuideEntity, String> {
    Optional<GuideEntity> findBySlugAndStatus(String slug, String status);
    boolean existsBySlug(String slug);
    List<GuideEntity> findByStatusOrderByPublishedAtDesc(String status);
}
