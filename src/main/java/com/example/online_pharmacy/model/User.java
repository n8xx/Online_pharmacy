package com.example.online_pharmacy.model;

import java.time.LocalDateTime;

public class User {
    private long id;
    private String login;
    private String passwordHash;
    private String email;
    private String phoneNumber;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String login, String email, String firstName, String lastName, Role role) {
        this();
        this.login = login;
        this.email = email;
        this.role = role;
    }
    

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPassword(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setPasswordHash(String passwordHash) {
    }

    public void setActive(boolean isActive) {

    }
}