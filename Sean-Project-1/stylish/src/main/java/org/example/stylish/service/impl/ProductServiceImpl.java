package org.example.stylish.service.impl;

import org.example.stylish.dao.*;
import org.example.stylish.dto.ProductRequest;
import org.example.stylish.model.*;
import org.example.stylish.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.base-url}")
    private String baseUrl;

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ColorDao colorDao;
    @Autowired
    private SizeDao sizeDao;
    @Autowired
    private VariantDao variantDao;
    @Autowired
    private ProductImageDao productImageDao;


    @Override
    public void addProduct(ProductRequest productRequest) {
        try {
            String mainImagePath = saveFile(productRequest.getMainImage());

            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile image : productRequest.getImages()) {
                imagePaths.add(saveFile(image));
            }

            String productTitle = productRequest.getTitle();
            Integer productId = productDao.getProductByTitle(productTitle);
            if (productId == null) {
                productId = productDao.createProduct(productRequest, mainImagePath);
            } else {
                productDao.updateProduct(productRequest, mainImagePath);
            }

            List<Integer> colorIdList = new ArrayList<>();
            for (int i = 0; i < productRequest.getName().size(); i++) {

                String colorName = productRequest.getName().get(i);
                String colorCode = productRequest.getCode().get(i);
                Integer colorId = colorDao.getColorIdByNameAndCode(colorName, colorCode);

                if (colorId == null) {
                    colorId = colorDao.createColor(colorName, colorCode);
                }
                colorIdList.add(colorId);
            }

            List<Integer> sizeIdList = new ArrayList<>();
            for (int i = 0; i < productRequest.getSize().size(); i++) {
                String sizeName = productRequest.getSize().get(i);
                Integer sizeId = sizeDao.getSizeByName(sizeName);
                if (sizeId == null) {
                    sizeId = sizeDao.createSize(sizeName);
                }
                sizeIdList.add(sizeId);
            }

            List<Integer> variantIdList = new ArrayList<>();
            for (int i = 0; i < colorIdList.size(); i++) {
                Integer colorId = colorIdList.get(i);
                Integer sizeId = sizeIdList.get(i);
                Integer stock = productRequest.getStock().get(i);
                Integer variantId = variantDao.getVariantIdByProductColorAndSize(productId, colorId, sizeId);
                if (variantId == null) {
                    variantId = variantDao.createVariant(productId, colorId, sizeId, stock);
                    variantIdList.add(variantId);
                } else {
                    variantId = variantDao.updateVariantStock(variantId, stock);
                    variantIdList.add(variantId);
                }
            }

            List<Integer> productImageIdList = new ArrayList<>();
            for (int i = 0; i < imagePaths.size(); i++) {
                Integer productImageId = productImageDao.creatProductImage(productId, imagePaths.get(i));
                productImageIdList.add(productImageId);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, Object> showProducts(String paging, String category) {

        int pageSize = 6;
        int limit = 7;
        int offset = Integer.parseInt(paging) * pageSize;

        List<ProductInfo> productInfoList = new ArrayList<>();
        switch (category) {
            case "men":
            case "women":
            case "accessories":
                productInfoList = productDao.getProductsByCategory(category, limit, offset);
                break;
            case "all":
                productInfoList = productDao.getAllProducts(limit, offset);
                break;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
        if (productInfoList.isEmpty()) {
            throw new NoSuchElementException("No products found for the given paging");
        }

        int productCount = productInfoList.size();
        Integer nextPaging = null;
        if (productCount > pageSize) {
            nextPaging = Integer.parseInt(paging) + 1;
            productCount = pageSize;
        }

        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            Integer productId = productInfoList.get(i).getId();
            List<String> imageUrlsList = productImageDao.getProductImagesById(productId);
            List<Variant> variants = variantDao.getVariantsByProductId(productId);
            List<String> sizeList = new ArrayList<>();
            Set<Color> colorSet = new HashSet<>();
            List<VariantInfo> variantInfoList = new ArrayList<>();
            for (int j = 0; j < variants.size(); j++) {
                Integer sizeId = variants.get(j).getSizeId();
                String size = sizeDao.getSizeById(sizeId);
                if (sizeList.contains(size) == false) {
                    sizeList.add(size);
                }
                Integer colorId = variants.get(j).getColorId();
                Color color = colorDao.getColorByColorId(colorId);
                colorSet.add(color);
                String code = color.getCode();
                Integer stock = variants.get(j).getStock();
                VariantInfo variantInfo = new VariantInfo(code, size, stock);
                variantInfoList.add(variantInfo);
            }
            Product product = new Product();
            product.setId(productId);
            product.setCategory(productInfoList.get(i).getCategory());
            product.setTitle(productInfoList.get(i).getTitle());
            product.setDescription(productInfoList.get(i).getDescription());
            product.setPrice(productInfoList.get(i).getPrice());
            product.setTexture(productInfoList.get(i).getTexture());
            product.setWash(productInfoList.get(i).getWash());
            product.setPlace(productInfoList.get(i).getPlace());
            product.setNote(productInfoList.get(i).getNote());
            product.setStory(productInfoList.get(i).getStory());
            product.setColors(colorSet);
            product.setSizes(sizeList);
            product.setVariants(variantInfoList);
            product.setMainImage(productInfoList.get(i).getMainImage());
            product.setImages(imageUrlsList);
            productList.add(product);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", productList);
        if (nextPaging != null) {
            result.put("next_paging", nextPaging);
        }
        return result;
    }

    @Override
    public Map<String, Object> searchProducts(String keyword, String paging) {
        int pageSize = 6;
        int limit = 7;
        int offset = Integer.parseInt(paging) * pageSize;
        List<ProductInfo> productInfoList = productDao.getProductsByKeyword(keyword, limit, offset);
        if (productInfoList.isEmpty()) {
            throw new NoSuchElementException("No products found.");
        }
        int productCount = productInfoList.size();
        Integer nextPaging = null;
        if (productCount > pageSize) {
            nextPaging = Integer.parseInt(paging) + 1;
            productCount = pageSize;
        }
        List<SearchProduct> serchProductList = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            serchProductList.add(getSearchProductRusult(productInfoList.get(i)));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", serchProductList);
        if (nextPaging != null) {
            result.put("next_paging", nextPaging);
        }
        return result;
    }

    @Override
    public Map<String, Object> showProductDetails(Integer productId) {
        List<ProductInfo> productInfoList = productDao.getProductById(productId);
        if (productInfoList.isEmpty()) {
            throw new NoSuchElementException("No products found.");
        }
        Product product = getProduct(productDao.getProductById(productId).get(0));
        Map<String, Object> result = new HashMap<>();
        result.put("data", product);
        return result;
    }

    private Product getProduct(ProductInfo productInfo) {
        List<String> imageUrlsList = productImageDao.getProductImagesById(productInfo.getId());
        List<Variant> variantsList = variantDao.getVariantsByProductId(productInfo.getId());
        Set<String> sizeSet = new HashSet<>();
        Set<Color> colorSet = new HashSet<>();
        List<VariantInfo> variantInfoList = new ArrayList<>();
        for (Variant variant : variantsList) {
            String size = sizeDao.getSizeById(variant.getSizeId());
            Color color = colorDao.getColorByColorId(variant.getColorId());
            sizeSet.add(size);
            colorSet.add(color);
            variantInfoList.add(new VariantInfo(color.getCode(),size , variant.getStock()));
        }
        Product product = new Product();
        product.setId(productInfo.getId());
        product.setCategory(productInfo.getCategory());
        product.setTitle(productInfo.getTitle());
        product.setDescription(productInfo.getDescription());
        product.setPrice(productInfo.getPrice());
        product.setTexture(productInfo.getTexture());
        product.setWash(productInfo.getWash());
        product.setPlace(productInfo.getPlace());
        product.setNote(productInfo.getNote());
        product.setStory(productInfo.getStory());
        product.setColors(colorSet);
        product.setSizes(sizeSet);
        product.setVariants(variantInfoList);
        product.setMainImage(productInfo.getMainImage());
        product.setImages(imageUrlsList);
        return product;
    }

    private SearchProduct getSearchProductRusult(ProductInfo productInfo) {
        List<String> imageUrlsList = productImageDao.getProductImagesById(productInfo.getId());
        List<Variant> variantsList = variantDao.getVariantsByProductId(productInfo.getId());
        Set<String> sizeSet = new HashSet<>();
        Set<Color> colorSet = new HashSet<>();
        List<VariantInfo> variantInfoList = new ArrayList<>();
        for (Variant variant : variantsList) {
            String size = sizeDao.getSizeById(variant.getSizeId());
            Color color = colorDao.getColorByColorId(variant.getColorId());
            sizeSet.add(size);
            colorSet.add(color);
            variantInfoList.add(new VariantInfo(size, color.getCode(), variant.getStock()));
        }
        SearchProduct searchProduct = new SearchProduct();
        searchProduct.setId(productInfo.getId());
        searchProduct.setCategory(productInfo.getCategory());
        searchProduct.setTitle(productInfo.getTitle());
        searchProduct.setDescription(productInfo.getDescription());
        searchProduct.setPrice(productInfo.getPrice());
        searchProduct.setTexture(productInfo.getTexture());
        searchProduct.setWash(productInfo.getWash());
        searchProduct.setPlace(productInfo.getPlace());
        searchProduct.setNote(productInfo.getNote());
        searchProduct.setStory(productInfo.getStory());
        searchProduct.setColors(colorSet);
        searchProduct.setSizes(sizeSet);
        searchProduct.setVariants(variantInfoList);
        searchProduct.setMainImage(productInfo.getMainImage());
        searchProduct.setImages(imageUrlsList);
        return searchProduct;
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());
        return baseUrl + "/" + fileName;
    }

}

