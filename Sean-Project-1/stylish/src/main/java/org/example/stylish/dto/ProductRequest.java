package org.example.stylish.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductRequest {
    private String category;
    private String title;
    private String description;
    private Integer price;
    private String texture;
    private String wash;
    private String place;
    private String note;
    private String story;
    private MultipartFile mainImage;
    private List<MultipartFile> images;
    private List<String> name;
    private List<String> code;
    private List<String> size;
    private List<Integer> stock;
}
