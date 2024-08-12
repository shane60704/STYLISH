package org.example.stylish.dto.facebookDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProfilePicture {
    @JsonProperty("data")
    private PictureInfo pictureInfo;
}
