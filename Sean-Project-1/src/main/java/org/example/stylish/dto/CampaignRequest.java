package org.example.stylish.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CampaignRequest {
    private Integer productId;
    private String category;
    private String productName;
    private String story;
    private MultipartFile picture;
}
