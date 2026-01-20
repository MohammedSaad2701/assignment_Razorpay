package com.example.payment.service;

import com.example.payment.dto.PaymentRazorpayResponse;
import com.example.payment.model.Order;
import com.example.payment.model.Payment;
import com.example.payment.model.enums.OrderStatus;
import com.example.payment.model.enums.PaymentStatus;
import com.example.payment.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final RazorpayClient razorpayClient;

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService, RazorpayClient razorpayClient) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.razorpayClient = razorpayClient;
    }

    private int toPaise(Double amount) {
        return (int) Math.round(amount * 100);
    }

    public PaymentRazorpayResponse createRazorpayPayment(String orderId, Double amount) throws RazorpayException {
        Order order = orderService.getOrder(orderId);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Payment can only be created for CREATED orders");
        }

        if (Math.abs(order.getTotalAmount() - amount) > 0.01) {
            throw new RuntimeException("Amount mismatch with order total");
        }

        // 1) Create Razorpay Order
        JSONObject req = new JSONObject();
        req.put("amount", toPaise(amount));
        req.put("currency", "INR");
        req.put("receipt", "rcpt_" + orderId);
        req.put("payment_capture", 1);

        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(req);
        String razorpayOrderId = razorpayOrder.get("id");

        // 2) Save Payment in DB (PENDING)
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentId(null); // will be filled after webhook
        payment.setCreatedAt(Instant.now());
        payment.setRazorpayOrderId(razorpayOrderId);

        paymentRepository.save(payment);

        // 3) Return to client
        PaymentRazorpayResponse response = new PaymentRazorpayResponse();
        response.setOrderId(orderId);
        response.setAmount(amount);
        response.setStatus(payment.getStatus().name());
        response.setRazorpayOrderId(razorpayOrderId);
        response.setPaymentId(payment.getId()); // local DB id (optional)

        return response;
    }
}
