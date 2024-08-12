package org.example.stylish.dao;

import java.util.List;

public interface ProductImageDao {
    Integer creatProductImage(Integer productId, String imageUrl);

    List<String> getProductImagesById(Integer productId);
}
