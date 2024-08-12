package org.example.stylish.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.stylish.dto.SignUpRequest;

import java.util.Map;

public interface UserService {
    Map<String, Object> signUp(SignUpRequest signUpRequest);

    Map<String, Object> signIn(Map<String, Object> requestBody);

    Map<String, Object> getUserProfile(HttpServletRequest request);

}
