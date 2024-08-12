package org.example.stylish.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"access_token", "access_expired", "user"})
public class SignInAndUpResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("access_expired")
    private Integer accessExpired;

    private UserInfoResponse user;
}
