package org.example.stylish.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecification {
    private int id;
    private String code;
    private String size;
    private int stock;
}
