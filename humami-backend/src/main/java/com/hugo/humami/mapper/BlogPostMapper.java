package com.hugo.humami.mapper;

import com.hugo.humami.domain.blog.BlogPostEntity;
import com.hugo.humami.dto.request.BlogPostRequest;
import com.hugo.humami.dto.response.BlogPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BlogPostMapper {

    BlogPostMapper INSTANCE = Mappers.getMapper(BlogPostMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BlogPostEntity toEntity(BlogPostRequest request);

    void updateFromRequest(@MappingTarget BlogPostEntity target, BlogPostRequest source);

    BlogPostResponse toResponse(BlogPostEntity entity);
}
