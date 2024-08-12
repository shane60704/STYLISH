package org.example.stylish.controller;

import org.example.stylish.dto.ProductRequest;
import org.example.stylish.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@ModelAttribute ProductRequest productRequest) {
        try {
            productService.addProduct(productRequest);
            return ResponseEntity.ok("新增成功!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增失敗!" + e.getMessage());
        }
    }

    @GetMapping("/api/1.0/products/{category}")
    public ResponseEntity<?> showProduct(@RequestParam(value = "paging", defaultValue = "0") String paging, @PathVariable String category) {
        try {
            Map<String, Object> result = productService.showProducts(paging, category);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/1.0/products/search")
    public ResponseEntity<?> searchProduct(@RequestParam(value = "keyword") String keyword,
                                           @RequestParam(value = "paging", defaultValue = "0") String paging) {
        try {
            Map<String, Object> result = productService.searchProducts(keyword, paging);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/1.0/products/details")
    public ResponseEntity<?> showProductDetails(@RequestParam(value = "id", defaultValue = "0") Integer productId) {
        try {
            Map<String, Object> result = productService.showProductDetails(productId);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
