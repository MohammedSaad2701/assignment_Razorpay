# Razorpay + Postman Setup (Only Required Parts)

## 1) `application.yaml` (Razorpay keys)
📌 File: `src/main/resources/application.yaml`

```yaml
server:
  port: 8080

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/ecommerce_db

razorpay:
  keyId: rzp_test_xxxxxxxxxxxx
  keySecret: xxxxxxxxxxxxxxxx
  webhookSecret: my_webhook_secret_123
```

### Where to get these values (Razorpay Dashboard)
- **keyId / keySecret**: Razorpay Dashboard → **Settings → API Keys → Generate Test Key**
- **webhookSecret**: Razorpay Dashboard → **Settings → Webhooks → Add New Webhook**
  - You set this secret yourself (example: `my_webhook_secret_123`)
  - Put the same value in `application.yaml`

---

## 2) `index.html` Razorpay Key Setup
📌 File: `src/main/resources/static/index.html`

In your Razorpay checkout options, set:

```js
key: "rzp_test_xxxxxxxxxxxx"
```

Example snippet:

```js
const options = {
  key: "rzp_test_xxxxxxxxxxxx",   // ONLY KeyId here
  order_id: paymentResp.razorpayOrderId,
  amount: Math.round(latestOrder.totalAmount * 100),
  currency: "INR",
  name: "Ecommerce Demo",
  description: "Order Payment",
  handler: function (response) {
    alert("Payment Success: " + response.razorpay_payment_id);
  }
};
```

⚠️ Never put `keySecret` in HTML/JS.

---

## 3) Postman Requests (Links + JSON Bodies)

### Base URL
```
http://localhost:8080
```

---

### A) Create Product
**POST** `http://localhost:8080/api/products`

```json
{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

---

### B) Get All Products
**GET** `http://localhost:8080/api/products`

---

### C) Add to Cart
**POST** `http://localhost:8080/api/cart/add`

```json
{
  "userId": "user123",
  "productId": "PASTE_PRODUCT_ID_HERE",
  "quantity": 2
}
```

---

### D) View Cart
**GET** `http://localhost:8080/api/cart/user123`

---

### E) Clear Cart
**DELETE** `http://localhost:8080/api/cart/user123/clear`

---

### F) Create Order From Cart
**POST** `http://localhost:8080/api/orders`

```json
{
  "userId": "user123"
}
```

---

### G) Get Order Details
**GET** `http://localhost:8080/api/orders/PASTE_ORDER_ID_HERE`

---

### H) Create Razorpay Payment Order (Backend creates Razorpay Order)
**POST** `http://localhost:8080/api/payments/razorpay/create`

```json
{
  "orderId": "PASTE_ORDER_ID_HERE",
  "amount": 100000.0
}
```

✅ Response contains `razorpayOrderId` which is used in Razorpay checkout.
