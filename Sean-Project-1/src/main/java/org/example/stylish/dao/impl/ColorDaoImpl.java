package org.example.stylish.dao.impl;

import org.example.stylish.dao.ColorDao;
import org.example.stylish.model.Color;
import org.example.stylish.dao.rowmapper.ColorRowMapper;
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
public class ColorDaoImpl implements ColorDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer getColorIdByNameAndCode(String colorName, String colorCode) {
        String sql = "SELECT id FROM color WHERE name = :color_name AND code = :color_code";
        Map<String, Object> map = new HashMap<>();
        map.put("colorName", colorName);
        map.put("colorCode", colorCode);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Integer createColor(String colorName, String colorcode) {
        String sql = "INSERT INTO color (name, code) VALUES (:name, :code)";
        Map<String, Object> map = new HashMap<>();
        map.put("name", colorName);
        map.put("code", colorcode);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public Color getColorByColorId(Integer colorId) {
        String sql = "SELECT name,code FROM color WHERE id = :colorId";
        Map<String, Object> map = new HashMap<>();
        map.put("colorId", colorId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new ColorRowMapper());
    }


}
