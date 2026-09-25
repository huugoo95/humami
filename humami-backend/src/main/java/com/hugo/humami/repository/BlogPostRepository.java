package com.hugo.humami.repository;

import com.hugo.humami.domain.blog.BlogPostEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends MongoRepository<BlogPostEntity, String> {
    Optional<BlogPostEntity> findBySlugAndStatus(String slug, String status);
    boolean existsBySlug(String slug);
    List<BlogPostEntity> findByStatusOrderByPublishedAtDesc(String status);
}
