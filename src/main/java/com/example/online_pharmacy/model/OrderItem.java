package com.example.online_pharmacy.model;

import java.math.BigDecimal;

public class OrderItem {
    private Order order;
    private Medicine medicine;
    private int quantity;
    private BigDecimal priceAtOrder;

    public OrderItem() {}
    
    public OrderItem(Medicine medicine, int quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.priceAtOrder = medicine.getPrice();
    }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(BigDecimal priceAtOrder) { 
        this.priceAtOrder = priceAtOrder; 
    }


    public BigDecimal getSubtotal() {
        return priceAtOrder.multiply(BigDecimal.valueOf(quantity));
    }
    
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = newQuantity;
    }
}