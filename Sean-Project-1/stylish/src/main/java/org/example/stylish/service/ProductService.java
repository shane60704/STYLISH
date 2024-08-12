package org.example.stylish.service;

import org.example.stylish.dto.ProductRequest;

import java.util.Map;

public interface ProductService {
    void addProduct(ProductRequest productRequest);

    Map<String, Object> showProducts(String paging, String category);

    Map<String, Object> searchProducts(String keyword, String paging);

    Map<String, Object> showProductDetails(Integer productId);
}
