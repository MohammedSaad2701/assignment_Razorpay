package com.example.payment.controller;

import com.example.payment.dto.CreateOrderRequest;
import com.example.payment.model.Order;
import com.example.payment.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order create(@RequestBody CreateOrderRequest req) {
        return orderService.createOrderFromCart(req.getUserId());
    }

    @GetMapping("/{orderId}")
    public Order get(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }
}