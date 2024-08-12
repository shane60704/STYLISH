package org.example.stylish.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.stylish.dao.UserDao;
import org.example.stylish.dto.*;
import org.example.stylish.dto.facebookDto.FacebookSignInRequest;
import org.example.stylish.dto.facebookDto.UserProfile;
import org.example.stylish.model.User;
import org.example.stylish.service.UserService;
import org.example.stylish.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper jacksonObjectMapper;
    private final Validator validator;

    @Autowired
    public UserServiceImpl(UserDao userDao, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, ObjectMapper jacksonObjectMapper, Validator validator) {
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.validator = validator;
    }

    @Override
    public Map<String, Object> signUp(SignUpRequest signUpRequest) {
        if (userDao.getNativeUserByEmailAndProvider(signUpRequest.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        User user = userDao.createNativeUser(signUpRequest);
        return generateJwtToken(user);
    }

    @Override
    public Map<String, Object> signIn(Map<String, Object> requestBody) {
        try {
            String provider = (String) requestBody.get("provider");
            switch (provider.toLowerCase()) {
                case "native":
                    NativeSignInRequest nativeSignInRequest = jacksonObjectMapper.convertValue(requestBody, NativeSignInRequest.class);
                    BindingResult nativeResult = new BeanPropertyBindingResult(nativeSignInRequest, "nativeSignInRequest");
                    validateRequest(nativeSignInRequest, nativeResult);
                    User nativeUser = userDao.getNativeUserByEmailAndProvider(nativeSignInRequest.getEmail());
                    if (nativeUser == null || !passwordEncoder.matches(nativeSignInRequest.getPassword(), nativeUser.getPassword())) {
                        throw new RuntimeException("Invalid email or password");
                    }
                    return generateJwtToken(nativeUser);
                case "facebook":
                    FacebookSignInRequest facebookSignInRequest = jacksonObjectMapper.convertValue(requestBody, FacebookSignInRequest.class);
                    BindingResult facebookResult = new BeanPropertyBindingResult(facebookSignInRequest, "facebookSignInRequest");
                    validateRequest(facebookSignInRequest, facebookResult);
                    RestTemplate restTemplate = new RestTemplate();
                    String url = "https://graph.facebook.com/me?fields=id,name,email,picture&access_token=" + facebookSignInRequest.getAccessToken();
                    UserProfile userProfile = restTemplate.getForObject(url, UserProfile.class);
                    if (userProfile == null || userProfile.getEmail() == null) {
                        throw new RuntimeException("Failed to fetch user profile from Facebook");
                    }
                    User facebookUser;
                    if (userDao.getFacebookUserByEmailAndProvider(userProfile.getEmail()) == null) {
                        facebookUser = userDao.createFacebookUser(userProfile);
                    } else {
                        facebookUser = userDao.UpdateFacebookUserProfile(userProfile);
                    }
                    return generateJwtToken(facebookUser);
                default:
                    throw new RuntimeException("Unsupported provider");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getUserProfile(HttpServletRequest request) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("provider", request.getAttribute("userProvider"));
        userProfile.put("name", request.getAttribute("userName"));
        userProfile.put("email", request.getAttribute("userEmail"));
        userProfile.put("picture", request.getAttribute("userPicture"));
        Map<String, Object> result = new HashMap<>();
        result.put("data", userProfile);
        return result;
    }

    private void validateRequest(Object request, BindingResult result) {
        validator.validate(request, result);
        if (result.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            response.put("errors", result.getAllErrors());
            throw new RuntimeException(response.toString());
        }
    }

    private Map<String, Object> generateJwtToken(User user) {
        Map<String, String> claims = new HashMap<>();
        claims.put("provider", user.getProvider());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("picture", user.getPicture() != null ? user.getPicture() : "null");
        String token = jwtUtil.generateToken(claims);
        int expiresIn = jwtUtil.getExpiration();
        Map<String, Object> result = new HashMap<>();
        UserInfoResponse userInfoResponse = new UserInfoResponse(user.getId(), user.getProvider(), user.getName(), user.getEmail(), user.getPicture());
        result.put("data", new SignInAndUpResponse(token, expiresIn, userInfoResponse));
        return result;
    }
}

