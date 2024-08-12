package org.example.stylish.dao.impl;

import org.example.stylish.dao.ProductDao;
import org.example.stylish.dto.ProductRequest;
import org.example.stylish.model.ProductInfo;
import org.example.stylish.dao.rowmapper.ProductInfoRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public Integer createProduct(ProductRequest productRequest, String mainImagePath) {
        String sql = "INSERT INTO product (category,title,description,price,texture,wash,place,note,story,main_image) VALUES " +
                "(:category,:title,:description,:price,:texture,:wash,:place,:note,:story,:mainImage)";
        Map<String, Object> map = new HashMap<>();
        map.put("category", productRequest.getCategory());
        map.put("title", productRequest.getTitle());
        map.put("description", productRequest.getDescription());
        map.put("price", productRequest.getPrice());
        map.put("texture", productRequest.getTexture());
        map.put("wash", productRequest.getWash());
        map.put("place", productRequest.getPlace());
        map.put("note", productRequest.getNote());
        map.put("story", productRequest.getStory());
        map.put("mainImage", mainImagePath);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(map);

        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void updateProduct(ProductRequest productRequest, String mainImagePath) {
        String sql = "UPDATE product SET " +
                "category = :category, " +
                "description = :description, " +
                "price = :price, " +
                "texture = :texture, " +
                "wash = :wash, " +
                "place = :place, " +
                "note = :note, " +
                "story = :story, " +
                "main_image = :mainImage " +
                "WHERE title = :title";
        Map<String, Object> map = new HashMap<>();
        map.put("category", productRequest.getCategory());
        map.put("description", productRequest.getDescription());
        map.put("price", productRequest.getPrice());
        map.put("texture", productRequest.getTexture());
        map.put("wash", productRequest.getWash());
        map.put("place", productRequest.getPlace());
        map.put("note", productRequest.getNote());
        map.put("story", productRequest.getStory());
        map.put("mainImage", mainImagePath);
        map.put("title", productRequest.getTitle());
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        namedParameterJdbcTemplate.update(sql, paramSource);
    }

    @Override
    public Integer getProductByTitle(String productTitle) {
        String sql = "SELECT id FROM product WHERE title = :title";
        Map<String, Object> map = new HashMap<>();
        map.put("title", productTitle);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductInfo> getProductsByCategory(String category, int limit, int offset) {
        String sql = "SELECT * FROM product WHERE category = :category LIMIT :limit OFFSET :offset";
        Map<String, Object> map = new HashMap<>();
        map.put("category", category);
        map.put("limit", limit);
        map.put("offset", offset);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new ProductInfoRowMapper());
    }

    @Override
    public List<ProductInfo> getAllProducts(int limit, int offset) {
        String sql = "SELECT * FROM product LIMIT :limit OFFSET :offset";
        Map<String, Object> map = new HashMap<>();
        map.put("limit", limit);
        map.put("offset", offset);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new ProductInfoRowMapper());
    }

    @Override
    public List<ProductInfo> getProductsByKeyword(String keyword,int limit, int offset){
        String sql = "SELECT * FROM product WHERE title LIKE CONCAT('%', :keyword, '%') LIMIT :limit OFFSET :offset";
        Map<String, Object> map = new HashMap<>();
        map.put("keyword", keyword);
        map.put("limit", limit);
        map.put("offset", offset);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new ProductInfoRowMapper());
    }
    
    @Override
    public List<ProductInfo> getProductById(Integer productId){
        String sql = "SELECT * FROM product WHERE id = :productId";
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.query(sql, paramSource, new ProductInfoRowMapper());
    }

}
