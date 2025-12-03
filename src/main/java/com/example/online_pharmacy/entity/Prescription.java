package com.example.online_pharmacy.entity;

import java.time.LocalDateTime;

public class Prescription {
    private User client;
    private User doctor;
    private Medicine medicine;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String diagnosis;
    private String dosageInstructions;
    private PrescriptionStatus status;
    private int refillsRemaining;
    
    // Конструкторы
    public Prescription() {
        this.issueDate = LocalDateTime.now();
        this.status = PrescriptionStatus.ACTIVE;
    }
    
    public Prescription(User client, User doctor, Medicine medicine, int validDays) {
        this();
        this.client = client;
        this.doctor = doctor;
        this.medicine = medicine;
        this.expiryDate = issueDate.plusDays(validDays);
    }
    
    // Геттеры и сеттеры
    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
    
    public User getDoctor() { return doctor; }
    public void setDoctor(User doctor) { this.doctor = doctor; }
    
    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }
    
    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
    
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getDosageInstructions() { return dosageInstructions; }
    public void setDosageInstructions(String dosageInstructions) { 
        this.dosageInstructions = dosageInstructions; 
    }
    
    public PrescriptionStatus getStatus() { return status; }
    public void setStatus(PrescriptionStatus status) { this.status = status; }
    
    public int getRefillsRemaining() { return refillsRemaining; }
    public void setRefillsRemaining(int refillsRemaining) { 
        this.refillsRemaining = refillsRemaining; 
    }
    
    // Бизнес-логика
    public boolean isValid() {
        return status == PrescriptionStatus.ACTIVE && 
               expiryDate != null && 
               expiryDate.isAfter(LocalDateTime.now()) &&
               refillsRemaining > 0;
    }
    
    public void useRefill() {
        if (!isValid()) {
            throw new IllegalStateException("Prescription is not valid");
        }
        refillsRemaining--;
        if (refillsRemaining == 0) {
            status = PrescriptionStatus.USED;
        }
    }
    
    public void extend(int additionalDays) {
        if (expiryDate != null) {
            expiryDate = expiryDate.plusDays(additionalDays);
        }
    }
}