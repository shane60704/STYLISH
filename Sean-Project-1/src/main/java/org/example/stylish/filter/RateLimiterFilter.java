package org.example.stylish.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.stylish.util.CacheUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;


public class RateLimiterFilter extends OncePerRequestFilter {

    private static final int TOO_MANY_REQUESTS_STATUS_CODE = 429;

    private final CacheUtil cacheUtil;

    public RateLimiterFilter(CacheUtil cacheUtil) {
        this.cacheUtil = cacheUtil;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String key = ip + ":" + uri;
        logger.info("Client IP Address: " + ip +", URI: " + uri );
        if (!isAllowed(key)) {
            response.setStatus(TOO_MANY_REQUESTS_STATUS_CODE);
            response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAllowed(String key) {
        Long currentCount = cacheUtil.increment(key);
        if (currentCount != null && currentCount == 1) {
            cacheUtil.expire(key, Duration.ofSeconds(1));
        }
        return currentCount != null && currentCount <= 10;
    }
}
