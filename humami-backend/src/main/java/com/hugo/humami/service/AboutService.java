package com.hugo.humami.service;

import com.hugo.humami.dto.request.AboutRequest;
import com.hugo.humami.dto.response.AboutResponse;

public interface AboutService {
    AboutResponse getAbout();
    AboutResponse updateAbout(AboutRequest request);
}
