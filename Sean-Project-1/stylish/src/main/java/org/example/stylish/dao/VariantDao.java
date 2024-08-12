package org.example.stylish.dao;

import org.example.stylish.model.ProductSpecification;
import org.example.stylish.model.Variant;
import org.example.stylish.model.VariantInfo;

import java.util.List;

public interface VariantDao {
    Integer getVariantIdByProductColorAndSize(Integer productId, Integer colorId, Integer sizeId);

    Integer createVariant(Integer productId, Integer colorId, Integer sizeId, Integer stock);

    Integer updateVariantStock(Integer variantId, Integer stock);

    List<Variant> getVariantsByProductId(Integer productId);

    List<ProductSpecification> getVariantInfo(Long productId, String colorCode, String size);
}
