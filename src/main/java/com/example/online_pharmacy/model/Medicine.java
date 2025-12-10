package com.example.online_pharmacy.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Medicine {
    private String name;
    private String description;
    private String dosage;
    private String manufacturer;
    private BigDecimal price;
    private boolean prescriptionRequired;
    private int quantityInStock;
    private String category;
    private LocalDate expirationDate;
    private boolean active;
    
    // Конструкторы
    public Medicine() {
        this.active = true;
    }
    
    public Medicine(String name, String dosage, BigDecimal price, 
                    boolean prescriptionRequired, int quantityInStock) {
        this();
        this.name = name;
        this.dosage = dosage;
        this.price = price;
        this.prescriptionRequired = prescriptionRequired;
        this.quantityInStock = quantityInStock;
    }
    
    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean prescriptionRequired) { 
        this.prescriptionRequired = prescriptionRequired; 
    }
    
    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { 
        this.quantityInStock = quantityInStock; 
    }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { 
        this.expirationDate = expirationDate; 
    }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    // Бизнес-логика
    public boolean isAvailable() {
        return active && quantityInStock > 0 && 
               (expirationDate == null || expirationDate.isAfter(LocalDate.now()));
    }
    
    public void reduceStock(int quantity) {
        if (quantity > quantityInStock) {
            throw new IllegalArgumentException("Not enough stock");
        }
        this.quantityInStock -= quantity;
    }
    
    public void addStock(int quantity) {
        this.quantityInStock += quantity;
    }
}