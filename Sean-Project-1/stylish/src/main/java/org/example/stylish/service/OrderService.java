package org.example.stylish.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.stylish.dto.orderDto.OrderRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<?> checkout(OrderRequest orderRequest, HttpServletRequest request) throws Exception;
}
