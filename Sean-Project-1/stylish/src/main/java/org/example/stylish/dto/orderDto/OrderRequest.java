package org.example.stylish.dto.orderDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String prime;
    private OrderInfoRequest order;
}
