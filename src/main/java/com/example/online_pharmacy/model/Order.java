package com.example.online_pharmacy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private User client;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private String deliveryAddress;
    private String notes;
    private List<OrderItem> items = new ArrayList<>();
    

    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.totalPrice = BigDecimal.ZERO;
    }
    
    public Order(User client) {
        this();
        this.client = client;
    }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { 
        this.paymentMethod = paymentMethod; 
    }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { 
        this.deliveryAddress = deliveryAddress; 
    }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.PAID;
    }
}