package com.hugo.humami.repository;

import com.hugo.humami.domain.AboutEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutRepository extends MongoRepository<AboutEntity, String> {
}
