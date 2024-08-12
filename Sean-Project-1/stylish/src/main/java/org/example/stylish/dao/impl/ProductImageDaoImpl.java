package org.example.stylish.dao.impl;

import org.example.stylish.dao.ProductImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductImageDaoImpl implements ProductImageDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer creatProductImage(Integer productId, String imageUrl) {
        String sql = "INSERT INTO product_image (product_id,image_url) VALUES (:productId,:imageUrl)";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("imageUrl", imageUrl);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<String> getProductImagesById(Integer productId) {
        String sql = "select image_url from product_image where product_id=:productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql, paramSource);
        List<String> imageUrls = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            imageUrls.add((String) row.get("image_url"));
        }
        return imageUrls;
    }

}
