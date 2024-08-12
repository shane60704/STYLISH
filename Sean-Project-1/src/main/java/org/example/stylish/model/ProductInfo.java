package org.example.stylish.model;

import lombok.Data;

@Data
public class ProductInfo {
    private Integer id;
    private String category;
    private String title;
    private String description;
    private Integer price;
    private String texture;
    private String wash;
    private String place;
    private String note;
    private String story;
    private String mainImage;
}
