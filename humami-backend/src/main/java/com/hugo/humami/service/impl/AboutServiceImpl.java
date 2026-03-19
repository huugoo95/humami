package com.hugo.humami.service.impl;

import com.hugo.humami.dto.response.AboutResponse;
import com.hugo.humami.service.AboutService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AboutServiceImpl implements AboutService {

    private final String title;
    private final String storyLine1;
    private final String storyLine2;
    private final String storyLine3;
    private final String photoUrl;
    private final String updatedAt;

    public AboutServiceImpl(
            @Value("${humami.about.title:La historia detrás de Humami}") String title,
            @Value("${humami.about.story-line-1:Humami nace de una mezcla personal entre tecnología y cocina de raíz.}") String storyLine1,
            @Value("${humami.about.story-line-2:Detrás del proyecto está Hugo, developer e hijo de profesora de cocina.}") String storyLine2,
            @Value("${humami.about.story-line-3:Desde Galicia, Humami une método y sabor para cocinar mejor cada día.}") String storyLine3,
            @Value("${humami.about.photo-url:}") String photoUrl,
            @Value("${humami.about.updated-at:2026-03-19T00:00:00Z}") String updatedAt
    ) {
        this.title = title;
        this.storyLine1 = storyLine1;
        this.storyLine2 = storyLine2;
        this.storyLine3 = storyLine3;
        this.photoUrl = photoUrl;
        this.updatedAt = updatedAt;
    }

    @Override
    public AboutResponse getAbout() {
        Instant parsedUpdatedAt;
        try {
            parsedUpdatedAt = Instant.parse(updatedAt);
        } catch (Exception ignored) {
            parsedUpdatedAt = Instant.now();
        }

        return new AboutResponse(
                title,
                List.of(storyLine1, storyLine2, storyLine3),
                photoUrl,
                parsedUpdatedAt
        );
    }
}
