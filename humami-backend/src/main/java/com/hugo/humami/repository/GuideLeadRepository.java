package com.hugo.humami.repository;

import com.hugo.humami.domain.guides.GuideLeadEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GuideLeadRepository extends MongoRepository<GuideLeadEntity, String> {
    Optional<GuideLeadEntity> findByGuideIdAndEmail(String guideId, String email);
}
