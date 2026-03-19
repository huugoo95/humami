package com.hugo.humami.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WriteAuthConfig implements WebMvcConfigurer {

    private final WriteAuthInterceptor writeAuthInterceptor;

    public WriteAuthConfig(WriteAuthInterceptor writeAuthInterceptor) {
        this.writeAuthInterceptor = writeAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(writeAuthInterceptor)
                .addPathPatterns("/api/meals/**", "/api/blog/**", "/api/about/**");
    }
}
