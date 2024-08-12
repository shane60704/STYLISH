package org.example.stylish.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.stylish.model.Color;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListRequest {
    private String id;
    private String name;
    private int price;
    private Color color;
    private String size;
    private int qty;
}
