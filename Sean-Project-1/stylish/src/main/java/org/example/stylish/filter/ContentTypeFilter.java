package org.example.stylish.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
public class ContentTypeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        if ("POST".equalsIgnoreCase(method) &&
                ("/api/1.0/user/signin".equals(requestURI) || "/api/1.0/user/signup".equals(requestURI) || "/api/1.0/order/checkout".equals(requestURI))) {
            String contentType = request.getContentType();
            log.info(contentType);
            if (contentType == null || !contentType.startsWith("application/json")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid Content-Type. Expected application/json\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}


