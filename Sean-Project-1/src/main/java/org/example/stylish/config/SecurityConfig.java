package org.example.stylish.config;

import org.apache.catalina.filters.RateLimitFilter;
import org.example.stylish.filter.ContentTypeFilter;
import org.example.stylish.filter.JwtAuthenticationFilter;
import org.example.stylish.filter.RateLimiterFilter;
import org.example.stylish.util.CacheUtil;
import org.example.stylish.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CacheUtil cacheUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/1.0/**", "/addProduct", "/assets/**").permitAll()
                        .requestMatchers("/admin/**", "/templates/**", "/user/**", "/*.html").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/1.0/user/profile", "/api/1.0/order/checkout").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(new RateLimiterFilter(cacheUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ContentTypeFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}








