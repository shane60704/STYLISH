package org.example.stylish.dto.facebookDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserProfile {
    private String id;
    private String name;
    private String email;
    @JsonProperty("picture")
    private ProfilePicture profilePicture;
}
