package org.example.stylish.dto.facebookDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FacebookSignInRequest {
    @NotBlank
    @JsonProperty("access_token")
    private String accessToken;
    @NotBlank
    private String provider;
}
