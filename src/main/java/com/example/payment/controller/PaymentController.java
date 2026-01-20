package com.Siddhesh.Ecommerce.controller;

import com.Siddhesh.Ecommerce.service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public String createOrder(@RequestParam("amount") int amount) throws RazorpayException {
        return paymentService.createRazorpayOrder(amount);
    }

    @PostMapping("/payment-callback")
    public RedirectView paymentCallback(
            @RequestParam("razorpay_order_id") String razorpayOrderId,
            @RequestParam("razorpay_payment_id") String razorpayPaymentId,
            @RequestParam("razorpay_signature") String razorpaySignature) throws RazorpayException {

        boolean isValid = paymentService.verifyRazorpaySignature(
                razorpayOrderId, razorpayPaymentId, razorpaySignature
        );

        if (isValid) {
            return new RedirectView("/success.html?orderId=" + razorpayOrderId);
        } else {
            return new RedirectView("/failure.html");
        }
    }

    @GetMapping("/get-key")
    public String getKey() {
        return paymentService.getRazorpayKeyId();
    }
}