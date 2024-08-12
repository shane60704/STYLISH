package org.example.stylish.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipientRequest {
    private String name;
    private String phone;
    private String email;
    private String address;
    private String time;
}
