package com.hugo.humami.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class WriteAuthInterceptor implements HandlerInterceptor {

    private static final Set<String> PROTECTED_METHODS = Set.of(
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name()
    );

    private static final String SECRET_HEADER = "X-HUMAMI-SECRET";

    @Value("${humami.auth.write-secret:}")
    private String writeSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!PROTECTED_METHODS.contains(request.getMethod())) {
            return true;
        }

        if (writeSecret == null || writeSecret.isBlank()) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Write auth secret is not configured");
            return false;
        }

        String providedSecret = request.getHeader(SECRET_HEADER);
        if (!writeSecret.equals(providedSecret)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return false;
        }

        return true;
    }
}
