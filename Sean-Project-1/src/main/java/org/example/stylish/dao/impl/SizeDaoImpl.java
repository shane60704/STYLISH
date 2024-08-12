package org.example.stylish.dao.impl;

import org.example.stylish.dao.SizeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SizeDaoImpl implements SizeDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer getSizeByName(String sizeName) {
        String sql = "SELECT id FROM size WHERE name = :size";
        Map<String, Object> map = new HashMap<>();
        map.put("size", sizeName);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Integer createSize(String sizeName) {
        String sql = "INSERT INTO size (name) VALUES (:name)";
        Map<String, Object> map = new HashMap<>();
        map.put("name", sizeName);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public String getSizeById(Integer sizeId) {
        String sql = "SELECT name FROM size WHERE id = :sizeId";
        Map<String, Object> map = new HashMap<>();
        map.put("sizeId", sizeId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.queryForObject(sql, paramSource, String.class);
    }

}
