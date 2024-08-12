package org.example.stylish.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@JsonPropertyOrder({"id", "category", "title", "description", "price", "texture", "wash", "place", "note", "story", "colors", "sizes", "variants", "main_image", "images"})
public class Product {
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
    private Set<Color> colors;
    private Collection<String> sizes;
    private List<VariantInfo> variants;
    @JsonProperty("main_image")
    private String mainImage;
    private List<String> images;
}
