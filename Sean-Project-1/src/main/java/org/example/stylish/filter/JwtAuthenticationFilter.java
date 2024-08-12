package org.example.stylish.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.example.stylish.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if ("/api/1.0/user/profile".equals(requestURI) || "/api/1.0/order/checkout".equals(requestURI)) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.info("No token provided or invalid header");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"No token provided\"}");
                return;
            }
            String jwt = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.validateToken(jwt);
                if (claims != null) {
                    String provider = claims.get("provider", String.class);
                    String name = claims.get("name", String.class);
                    String email = claims.get("email", String.class);
                    String picture = claims.get("picture", String.class);

                    UserDetails userDetails = User.withUsername(email)
                            .password("")
                            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                            .build();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    request.setAttribute("userProvider", provider);
                    request.setAttribute("userName", name);
                    request.setAttribute("userEmail", email);
                    request.setAttribute("userPicture", picture);
                    log.info("9: " + request);
                } else {
                    log.info("Invalid token provided");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Invalid token\"}");
                    return;
                }
            } catch (Exception e) {
                log.error("Token validation error", e);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid token\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

