package com.hugo.humami.service;

import java.io.IOException;

public interface S3Service {
    String getTempUrl(String key) throws IOException;

}