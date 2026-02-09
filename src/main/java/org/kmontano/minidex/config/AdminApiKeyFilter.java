package org.kmontano.minidex.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AdminApiKeyFilter extends OncePerRequestFilter {

    @Value("${admin.api.key}")
    private String adminKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/") &&
                request.getRequestURI().contains("/admin/")) {

            if (!request.getMethod().equals("POST")){
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }

            String key = request.getHeader("X-ADMIN-KEY");

            if (!adminKey.equals(key)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

