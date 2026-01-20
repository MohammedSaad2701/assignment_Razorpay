package com.example.payment.controller;

import com.example.payment.dto.AddToCartRequest;
import com.example.payment.model.CartItem;
import com.example.payment.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartItem add(@RequestBody AddToCartRequest req) {
        return cartService.addToCart(req);
    }

    @GetMapping("/{userId}")
    public List<CartItem> get(@PathVariable String userId) {
        return cartService.getUserCart(userId);
    }

    @DeleteMapping("/{userId}/clear")
    public Map<String, String> clear(@PathVariable String userId) {
        cartService.clearCart(userId);
        return Map.of("message", "Cart cleared successfully");
    }
}
