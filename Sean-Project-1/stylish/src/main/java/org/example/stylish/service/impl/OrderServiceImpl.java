package org.example.stylish.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.stylish.dao.OrderDao;
import org.example.stylish.dao.UserDao;
import org.example.stylish.dao.VariantDao;
import org.example.stylish.dto.orderDto.OrderRequest;
import org.example.stylish.dto.orderDto.ShoppingListRequest;
import org.example.stylish.model.ProductSpecification;
import org.example.stylish.model.User;
import org.example.stylish.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Value("${tappay.partner_key}")
    private String partnerKey;

    @Value("${tappay.merchant_id}")
    private String merchantId;

    private final VariantDao variantDao;
    private final UserDao userDao;
    private final OrderDao orderDao;

    @Autowired
    public OrderServiceImpl(VariantDao variantDao, UserDao userDao, OrderDao orderDao) {
        this.variantDao = variantDao;
        this.userDao = userDao;
        this.orderDao = orderDao;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseEntity<?> checkout(OrderRequest orderRequest, HttpServletRequest request) throws Exception {

        Map<String, Object> response = new HashMap<>();
        List<ShoppingListRequest> shoppingList = orderRequest.getOrder().getList();
        List<Integer> variantIdList = new ArrayList<>();
        List<Integer> productAmountList = new ArrayList<>();
        List<Integer> productStockList = new ArrayList<>();

        for (int i = 0; i < shoppingList.size(); i++) {
            Long productId = Long.parseLong(shoppingList.get(i).getId());
            int orderQty = shoppingList.get(i).getQty();
            String size = shoppingList.get(i).getSize();
            String colorCode = shoppingList.get(i).getColor().getCode();

            // Check if the product exists
            List<ProductSpecification> productSpecificatioList = variantDao.getVariantInfo(productId, colorCode , size);
            log.info(productSpecificatioList.get(i).toString());
            if (productSpecificatioList.isEmpty()) {
                throw new IllegalArgumentException("Product not exist");
            } else {
                variantIdList.add(productSpecificatioList.get(0).getId());
            }

            // Check if the stock is sufficient
            int stock = productSpecificatioList.get(0).getStock();
            if (stock < orderQty) {
                throw new IllegalArgumentException("product not enough");
            } else {
                productStockList.add(stock);
                productAmountList.add(stock - orderQty);
            }
        }

        // Retrieve the order placer's user id
        String userProvider = (String) request.getAttribute("userProvider");
        String userEmail = (String) request.getAttribute("userEmail");
        User user = new User();
        if (userProvider.equals("facebook")) {
            user = userDao.getFacebookUserByEmailAndProvider(userEmail);
        } else {
            user = userDao.getNativeUserByEmailAndProvider(userEmail);
        }

        //Insert data into the recipient table and retrieve the recipient id
        Integer recipientId = orderDao.createRecipient(orderRequest);
        if (recipientId == null) {
            throw new IllegalArgumentException("Order failed");
        }

        //Insert data into the order table and retrieve the order id
        Integer orderId = orderDao.createOrder(orderRequest, user.getId(), recipientId);
        if (orderId == null) {
            throw new IllegalArgumentException("Order failed");
        }

        for (int i = 0; i < variantIdList.size(); i++) {
            //Insert data into the order_details table
            int orderDetailsRowsAffected = orderDao.createOrderDetails(orderId, variantIdList.get(i), shoppingList.get(i).getQty());
            if (orderDetailsRowsAffected != 1) {
                throw new IllegalArgumentException("Order failed");
            }
            //update stock
            Integer variantRowsAffected = variantDao.updateVariantStock(variantIdList.get(i), productAmountList.get(i));
            if (variantRowsAffected == null) {
                throw new IllegalArgumentException("Order failed");
            }
        }

        //Enter the payment process , status equal 0 indicates a successful payment
        if (tapPay(orderRequest) == 0) {
            int completePayment = 1;
            // update payment status (default = 0)
            int OrderRowsAffected = orderDao.updateOrderPaymentStatus(orderId, completePayment);
            if (OrderRowsAffected != 1) {
                throw new IllegalArgumentException("Order failed");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("number", orderId);
            response.put("data", data);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("error", "order failed");
        return ResponseEntity.badRequest().body(response);
    }

    public int tapPay(OrderRequest orderRequest) {
        String url = "https://sandbox.tappaysdk.com/tpc/payment/pay-by-prime";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-api-key", partnerKey);
        Map<String, Object> cardholder = new HashMap<>();
        cardholder.put("phone_number", orderRequest.getOrder().getRecipient().getPhone());
        cardholder.put("name", orderRequest.getOrder().getRecipient().getName());
        cardholder.put("email", orderRequest.getOrder().getRecipient().getEmail());
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prime", orderRequest.getPrime());
        requestBody.put("partner_key", partnerKey);
        requestBody.put("merchant_id", merchantId);
        requestBody.put("amount", orderRequest.getOrder().getTotal());
        requestBody.put("details", "Payment for order");
        requestBody.put("cardholder", cardholder);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return (Integer) response.getBody().get("status");
    }
}

