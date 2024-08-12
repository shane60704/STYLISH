package org.example.stylish.dao;

import org.example.stylish.dto.SignUpRequest;
import org.example.stylish.dto.facebookDto.UserProfile;
import org.example.stylish.model.User;


public interface UserDao {
    User createNativeUser(SignUpRequest signUpRequest);

    User createFacebookUser(UserProfile userProfile);

    User getNativeUserByEmailAndProvider(String email);

    User getFacebookUserByEmailAndProvider(String email);

    User getUserById(Integer userId);

    User UpdateFacebookUserProfile(UserProfile userProfile);

}
