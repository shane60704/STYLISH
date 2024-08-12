package org.example.stylish.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"color_code", "size", "stock"})
public class VariantInfo {
    @JsonProperty("color_code")
    private String colorCode;
    private String size;
    private Integer stock;
}
