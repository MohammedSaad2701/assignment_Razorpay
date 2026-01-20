package com.example.payment.controller;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentRazorpayResponse;
import com.example.payment.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/razorpay/create")
    public PaymentRazorpayResponse createRazorpay(@RequestBody PaymentRequest req) throws RazorpayException {
        return paymentService.createRazorpayPayment(req.getOrderId(), req.getAmount());
    }
}
