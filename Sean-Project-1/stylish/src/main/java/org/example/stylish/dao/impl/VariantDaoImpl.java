package org.example.stylish.dao.impl;

import org.example.stylish.dao.VariantDao;
import org.example.stylish.model.ProductSpecification;
import org.example.stylish.model.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VariantDaoImpl implements VariantDao {

    private static final Logger log = LoggerFactory.getLogger(VariantDaoImpl.class);
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer getVariantIdByProductColorAndSize(Integer productId, Integer colorId, Integer sizeId) {
        String sql = "SELECT id FROM variant WHERE product_id = :productId AND color_id = :colorId AND size_id = :sizeId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("colorId", colorId);
        map.put("sizeId", sizeId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Override
    public Integer createVariant(Integer productId, Integer colorId, Integer sizeId, Integer stock) {
        String sql = "INSERT INTO variant (product_id,color_id,size_id,stock) VALUES " +
                "(:productId,:colorId,:sizeId,:stock)";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("colorId", colorId);
        map.put("sizeId", sizeId);
        map.put("stock", stock);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Override
    public Integer updateVariantStock(Integer variantId, Integer stock) {
        String sql = "UPDATE variant SET stock = :stock WHERE id = :id";
        Map<String, Object> map = new HashMap<>();
        map.put("stock", stock);
        map.put("id", variantId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        int rowsAffected = namedParameterJdbcTemplate.update(sql, paramSource);
        if (rowsAffected > 0) {
            return variantId;
        } else {
            return null;
        }
    }

    @Override
    public List<Variant> getVariantsByProductId(Integer productId) {
        String sql = "SELECT color_id,size_id,stock FROM variant WHERE product_id = :productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new BeanPropertyRowMapper<>(Variant.class));
    }

    @Override
    public List<ProductSpecification> getVariantInfo(Long productId, String colorCode, String size) {
        String sql = "SELECT " +
                "    variant.id,  " +
                "    color.code AS code, " +
                "    size.name AS size, " +
                "    variant.stock " +
                "FROM " +
                "    product " +
                "JOIN " +
                "    variant ON product.id = variant.product_id " +
                "JOIN " +
                "    size ON variant.size_id = size.id " +
                "JOIN " +
                "    color ON variant.color_id = color.id " +
                "WHERE " +
                "    product.id = :productId " +
                "AND " +
                "     color.code = :colorCode" +
                " AND " +
                "    size.name = :size";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("colorCode", colorCode);
        map.put("size", size);
        try {
            return namedParameterJdbcTemplate.query(sql, map, new BeanPropertyRowMapper<>(ProductSpecification.class));
        } catch (DataAccessException e) {
            log.info(e.getMessage());
            return null;
        }
    }

}
