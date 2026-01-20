package com.example.payment.service;

import com.example.payment.dto.AddToCartRequest;
import com.example.payment.model.CartItem;
import com.example.payment.model.Product;
import com.example.payment.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    public CartItem addToCart(AddToCartRequest req) {
        Product product = productService.getById(req.getProductId());

        if (product.getStock() == null || product.getStock() <= 0) {
            throw new RuntimeException("Product out of stock");
        }

        return cartRepository.findByUserIdAndProductId(req.getUserId(), req.getProductId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + req.getQuantity());
                    return cartRepository.save(existing);
                })
                .orElseGet(() -> {
                    CartItem item = new CartItem();
                    item.setUserId(req.getUserId());
                    item.setProductId(req.getProductId());
                    item.setQuantity(req.getQuantity());
                    return cartRepository.save(item);
                });
    }

    public List<CartItem> getUserCart(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}