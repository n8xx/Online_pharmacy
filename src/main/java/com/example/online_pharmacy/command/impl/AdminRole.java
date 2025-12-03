package com.example.online_pharmacy.command.impl;

import com.example.online_pharmacy.command.UserRole;
import com.example.online_pharmacy.entity.User;
import com.example.online_pharmacy.entity.Medicine;
import com.example.online_pharmacy.entity.Order;
import com.example.online_pharmacy.entity.Prescription;
import org.apache.log4j.LogManager;

import java.util.logging.Logger;



public class AdminRole implements UserRole {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void viewMedicines(User user) {
        logger.info("Admin is viewing medicine catalog");
    }
    
    @Override
    public void purchaseMedicine(User user, Medicine medicine, int quantity) {
        throw new UnsupportedOperationException("Admin cannot purchase medicines");
    }
    
    @Override
    public void manageOrders(User user, Order order) {
        logger.info("Admin {} is managing order #{}", user.getLogin(), order.getId());
        // Админ может просматривать, отменять, изменять статус любых заказов
    }
    
    @Override
    public void managePrescriptions(User user, Prescription prescription) {
        logger.info("Admin {} is managing prescription #{}", user.getLogin(), prescription.getId());
        // Админ может просматривать все рецепты, но не назначать новые
    }
    
    @Override
    public void manageMedicineCatalog(User user, Medicine medicine) {
        logger.info("Admin {} is managing medicine #{}", user.getLogin(), medicine.getId());
        // Админ может добавлять, редактировать, удалять лекарства
    }
    
    @Override
    public void manageUsers(User user, User targetUser) {
        logger.info("Admin {} is managing user {}", user.getLogin(), targetUser.getLogin());

    }
    
    @Override
    public void requestPrescription(User user) {
        throw new UnsupportedOperationException("Admin cannot request prescriptions");
    }
    
    @Override
    public String getRoleName() {
        return "ADMIN";
    }
    
    @Override
    public boolean hasPermission(String action) {
        return true;
    }
    
    @Override
    public String toString() {
        return "AdminRole";
    }
}