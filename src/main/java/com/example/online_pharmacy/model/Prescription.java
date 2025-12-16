package com.example.online_pharmacy.model;

import java.time.LocalDateTime;

public class Prescription {
    private long id;
    private User client;
    private User doctor;
    private Medicine medicine;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private PrescriptionStatus status;

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


}