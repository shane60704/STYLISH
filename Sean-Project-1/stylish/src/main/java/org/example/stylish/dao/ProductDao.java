package org.example.stylish.dao;

import org.example.stylish.dto.ProductRequest;
import org.example.stylish.model.ProductInfo;

import java.util.List;

public interface ProductDao {
    Integer createProduct(ProductRequest productRequest, String mainImagePath);

    void updateProduct(ProductRequest productRequest, String mainImagePath);

    Integer getProductByTitle(String productTitle);

    List<ProductInfo> getProductsByCategory(String category, int limit, int offset);

    List<ProductInfo> getAllProducts(int limit, int offset);

    List<ProductInfo> getProductsByKeyword(String keyword, int limit, int offset);

    List<ProductInfo> getProductById(Integer productId);

}

