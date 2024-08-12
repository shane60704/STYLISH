package org.example.stylish.dao;

import org.example.stylish.dto.orderDto.OrderRequest;

public interface OrderDao {
    Integer createRecipient(OrderRequest orderRequest);
    Integer createOrder(OrderRequest orderRequest,int userId,int recipientId);
    int createOrderDetails(int orderId,int variantId,int qty);
    int updateOrderPaymentStatus(int orderId,int paymentStatus);
}
