package org.example.stylish.dao.impl;

import org.example.stylish.dao.AdminDao;
import org.example.stylish.dto.CampaignRequest;
import org.example.stylish.model.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminDaoImpl implements AdminDao {

    private static final Logger log = LoggerFactory.getLogger(AdminDaoImpl.class);
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public AdminDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public int createCampaign(CampaignRequest campaignRequest, String campaignPicturePath) {
        String sql = "INSERT INTO campaign (picture,story,product_id) VALUES (:picture,:story,:productId)";
        Map<String, Object> map = new HashMap<>();
        map.put("picture", campaignPicturePath);
        map.put("story", campaignRequest.getStory());
        map.put("productId", campaignRequest.getProductId());
        try {
            return namedParameterJdbcTemplate.update(sql, map);
        } catch (DataAccessException e) {
            log.error("Error accessing the database", e);
            throw new RuntimeException("Error accessing the database", e);
        }
    }

    @Override
    public int updateCampaign(CampaignRequest campaignRequest, String campaignPicturePath) {
        String sql = "UPDATE campaign SET picture = :picture,story = :story WHERE product_id = :productId";
        Map<String, Object> map = new HashMap<>();
        map.put("picture", campaignPicturePath);
        map.put("story", campaignRequest.getStory());
        map.put("productId", campaignRequest.getProductId());
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.update(sql, paramSource);
        } catch (DataAccessException e) {
            log.error("Error accessing the database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Campaign getCampaignByProductId(int productId) {
        String sql = "SELECT product_id,picture,story FROM campaign WHERE product_id = :productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(Campaign.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Campaign> getAllCampaigns() {
        String sql = "SELECT product_id AS productId, picture, story FROM campaign";
        try {
            return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Campaign.class));
        } catch (DataAccessException e) {
            log.error("Error accessing the database", e);
            throw new RuntimeException(e);
        }
    }

}
