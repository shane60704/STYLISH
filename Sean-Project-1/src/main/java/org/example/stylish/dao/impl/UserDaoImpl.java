package org.example.stylish.dao.impl;

import org.example.stylish.dao.UserDao;
import org.example.stylish.dto.SignUpRequest;
import org.example.stylish.dto.facebookDto.UserProfile;
import org.example.stylish.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, PasswordEncoder passwordEncoder) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createNativeUser(SignUpRequest signUpRequest) {
        String sql = "INSERT INTO user (name,email,password) VALUES (:name,:email,:password)";
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        Map<String, Object> map = new HashMap<>();
        map.put("name", signUpRequest.getName());
        map.put("email", signUpRequest.getEmail());
        map.put("password", encodedPassword);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        Integer userId = keyHolder.getKey().intValue();
        return getUserById(userId);
    }

    @Override

    public User getNativeUserByEmailAndProvider(String email) {
        String sql = "SELECT * FROM user WHERE email = :email AND provider = :provider";
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("provider", "native");
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(User.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User createFacebookUser(UserProfile userProfile) {
        String sql = "INSERT INTO user (name,email,provider,picture) VALUES (:name,:email,:provider,:picture)";
        Map<String, Object> map = new HashMap<>();
        map.put("name", userProfile.getName());
        map.put("email", userProfile.getEmail());
        map.put("provider", "facebook");
        map.put("picture", userProfile.getProfilePicture().getPictureInfo().getUrl());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        Integer userId = keyHolder.getKey().intValue();
        return getUserById(userId);
    }

    @Override
    public User getFacebookUserByEmailAndProvider(String email) {
        String sql = "SELECT id,provider,name,email,picture FROM user WHERE email = :email AND provider = :provider";
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("provider", "facebook");
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(User.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User UpdateFacebookUserProfile(UserProfile userProfile) {
        String sql = "UPDATE user SET name = :name, email = :email, picture = :picture WHERE email = :email";
        Map<String, Object> map = new HashMap<>();
        map.put("name", userProfile.getName());
        map.put("email", userProfile.getEmail());
        map.put("picture", userProfile.getProfilePicture().getPictureInfo().getUrl());
        namedParameterJdbcTemplate.update(sql, map);
        return getFacebookUserByEmailAndProvider(userProfile.getEmail());
    }

    @Override
    public User getUserById(Integer userId) {
        String sql = "SELECT * FROM user WHERE id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(User.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }




}
