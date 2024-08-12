package org.example.stylish.dto.facebookDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PictureInfo {
    private int height;
    @JsonProperty("is_silhouette")
    private boolean isSilhouette;
    private String url;
    private int width;
}
