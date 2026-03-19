package com.hugo.humami.service;

import com.hugo.humami.dto.request.AboutRequest;
import com.hugo.humami.dto.response.AboutResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AboutService {
    AboutResponse getAbout();
    AboutResponse updateAbout(AboutRequest request);
    AboutResponse updateAboutImage(MultipartFile image) throws IOException;
}
