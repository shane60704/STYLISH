package org.example.stylish.dao.rowmapper;

import org.example.stylish.model.ProductInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductInfoRowMapper implements RowMapper<ProductInfo> {
    public ProductInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setId(rs.getInt("id"));
        productInfo.setCategory(rs.getString("category"));
        productInfo.setTitle(rs.getString("title"));
        productInfo.setDescription(rs.getString("description"));
        productInfo.setPrice(rs.getInt("price"));
        productInfo.setTexture(rs.getString("texture"));
        productInfo.setWash(rs.getString("wash"));
        productInfo.setPlace(rs.getString("place"));
        productInfo.setNote(rs.getString("note"));
        productInfo.setStory(rs.getString("story"));
        productInfo.setMainImage(rs.getString("main_image"));
        return productInfo;
    }
}
