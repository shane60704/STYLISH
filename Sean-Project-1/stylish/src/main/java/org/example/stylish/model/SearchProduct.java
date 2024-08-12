package org.example.stylish.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "category", "title", "description", "price", "texture", "wash", "place", "note", "story", "main_image", "images", "variants", "colors", "sizes"})
public class SearchProduct {
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
