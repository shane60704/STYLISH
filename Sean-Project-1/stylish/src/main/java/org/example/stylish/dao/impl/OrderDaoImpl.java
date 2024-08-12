package org.example.stylish.dao.impl;

import org.example.stylish.dao.OrderDao;
import org.example.stylish.dto.orderDto.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class OrderDaoImpl implements OrderDao {
    private static final Logger log = LoggerFactory.getLogger(OrderDaoImpl.class);

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public OrderDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Integer createRecipient(OrderRequest orderRequest) {
        String sql = "INSERT INTO recipient (name,phone,email,address,time) VALUES (:name,:phone,:email,:address,:time)";
        Map<String, Object> map = new HashMap<>();
        map.put("name", orderRequest.getOrder().getRecipient().getName());
        map.put("phone", orderRequest.getOrder().getRecipient().getPhone());
        map.put("email", orderRequest.getOrder().getRecipient().getEmail());
        map.put("address", orderRequest.getOrder().getRecipient().getAddress());
        map.put("time", orderRequest.getOrder().getRecipient().getTime());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder, new String[]{"id"});
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Override
    public Integer createOrder(OrderRequest orderRequest, int userId, int recipientId) {
        String sql = "INSERT INTO `order` (user_id,recipient_id,shipping,payment,subtotal,freight,total) VALUES" +
                "(:userId,:recipientId,:shipping,:payment,:subtotal,:freight,:total)";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("recipientId", recipientId);
        map.put("shipping", orderRequest.getOrder().getShipping());
        map.put("payment", orderRequest.getOrder().getPayment());
        map.put("subtotal", orderRequest.getOrder().getSubtotal());
        map.put("freight", orderRequest.getOrder().getFreight());
        map.put("total", orderRequest.getOrder().getTotal());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder, new String[]{"id"});
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Override
    public int createOrderDetails(int orderId, int variantId, int qty) {
        String sql = "INSERT INTO order_details (order_id,variant_id,qty) VALUES (:orderId,:variantId,:qty)";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        map.put("variantId", variantId);
        map.put("qty", qty);
        try {
            return namedParameterJdbcTemplate.update(sql, map);
        } catch (DataAccessException e) {
            log.info(e.getMessage());
            throw new RuntimeException("Failed to create order details.");
        }
    }

    @Override
    public int updateOrderPaymentStatus(int orderId, int paymentStatus) {
        String sql = "UPDATE `order` SET payment_status = :paymentStatus WHERE id = :OrderId";
        Map<String, Object> map = new HashMap<>();
        map.put("paymentStatus", paymentStatus);
        map.put("OrderId", orderId);
        return namedParameterJdbcTemplate.update(sql, map);
    }

}