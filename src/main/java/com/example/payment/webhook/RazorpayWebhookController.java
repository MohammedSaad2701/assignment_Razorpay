package com.example.payment.webhook;

import com.example.payment.model.Payment;
import com.example.payment.model.enums.OrderStatus;
import com.example.payment.model.enums.PaymentStatus;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.service.OrderService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/webhooks")
public class RazorpayWebhookController {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Value("${razorpay.webhookSecret}")
    private String webhookSecret;

    public RazorpayWebhookController(PaymentRepository paymentRepository, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    @PostMapping("/razorpay")
    public String handleRazorpayWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payload
    ) {
        // 1) verify signature
        if (!verifySignature(payload, signature, webhookSecret)) {
            throw new RuntimeException("Invalid Razorpay webhook signature");
        }

        // 2) parse payload
        JSONObject json = new JSONObject(payload);
        String event = json.optString("event");

        if (!event.equals("payment.captured") && !event.equals("payment.failed")) {
            return "Ignored event: " + event;
        }

        JSONObject paymentEntity = json.getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId = paymentEntity.getString("order_id");
        String razorpayPaymentId = paymentEntity.getString("id");

        Payment payment = paymentRepository.findAll().stream()
                .filter(p -> razorpayOrderId.equals(p.getRazorpayOrderId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment not found for razorpayOrderId: " + razorpayOrderId));

        if (event.equals("payment.captured")) {
            payment.setStatus(PaymentStatus.SUCCESS);
            orderService.updateStatus(payment.getOrderId(), OrderStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            orderService.updateStatus(payment.getOrderId(), OrderStatus.FAILED);
        }

        payment.setPaymentId(razorpayPaymentId);
        paymentRepository.save(payment);

        return "Webhook processed";
    }

    private boolean verifySignature(String payload, String actualSignature, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(keySpec);

            byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hash);

            return expectedSignature.equals(actualSignature);
        } catch (Exception e) {
            return false;
        }
    }
}
