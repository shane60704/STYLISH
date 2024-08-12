package org.example.stylish.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoRequest {
    private String shipping;
    private String payment;
    private int subtotal;
    private int freight;
    private int total;
    private RecipientRequest recipient;
    private List<ShoppingListRequest> list;
}
