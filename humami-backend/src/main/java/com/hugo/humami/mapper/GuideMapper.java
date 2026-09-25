package com.hugo.humami.mapper;

import com.hugo.humami.domain.guides.GuideEntity;
import com.hugo.humami.dto.request.GuideRequest;
import com.hugo.humami.dto.response.GuideResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GuideMapper {
    GuideEntity toEntity(GuideRequest request);
    GuideResponse toResponse(GuideEntity entity);
    void updateFromRequest(GuideRequest request, @MappingTarget GuideEntity entity);
}
