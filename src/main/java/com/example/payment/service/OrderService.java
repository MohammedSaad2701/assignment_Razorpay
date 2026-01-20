package com.example.payment.service;

import com.example.payment.model.CartItem;
import com.example.payment.model.Order;
import com.example.payment.model.OrderItem;
import com.example.payment.model.Product;
import com.example.payment.model.Order;
import com.example.payment.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, CartService cartService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
    }

    public Order createOrderFromCart(String userId) {
        List<CartItem> cartItems = cartService.getUserCart(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        // validate stock + calculate total
        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            double price = product.getPrice();
            total += price * cartItem.getQuantity();

            orderItems.add(new OrderItem(product.getId(), cartItem.getQuantity(), price));
        }

        // reduce stock
        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            product.setStock(product.getStock() - cartItem.getQuantity());
            productService.save(product);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus(Order.CREATED);
        order.setCreatedAt(Instant.now());
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);

        // clear cart
        cartService.clearCart(userId);

        return saved;
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    public Order updateStatus(String orderId, OrderService service) {
        Order order = getOrder(orderId);
        order.setStatus(service);
        return orderRepository.save(order);
    }
}