package com.example.payment.dto;

public class PaymentRazorPayResponse {
    private String paymentId;
    private String orderId;
    private Double amount;
    private String status;
    private String razorpayOrderId;

    public RazorpayPaymentResponse() {}

    public RazorpayPaymentResponse(String paymentId, String orderId, Double amount, String status, String razorpayOrderId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
}