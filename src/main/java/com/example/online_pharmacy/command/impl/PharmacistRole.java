package com.example.online_pharmacy.command.impl;

import com.onlinepharmacy.command.UserRole;
import com.onlinepharmacy.entity.User;
import com.onlinepharmacy.entity.Medicine;
import com.onlinepharmacy.entity.Order;
import com.onlinepharmacy.entity.Prescription;
import com.onlinepharmacy.dto.PrescriptionRequestDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Реализация роли фармацевта.
 * Фармацевт управляет заказами и каталогом лекарств, проверяет рецепты.
 */
public class PharmacistRole implements UserRole {
    private static final Logger logger = LogManager.getLogger();
    
    @Override
    public void viewMedicines(User user) {
        logger.info("Pharmacist {} is viewing medicine catalog", user.getLogin());
        // Фармацевт видит полный каталог с остатками на складе
    }
    
    @Override
    public void purchaseMedicine(User user, Medicine medicine, int quantity) {
        throw new UnsupportedOperationException("Pharmacist cannot purchase medicines");
    }
    
    @Override
    public void manageOrders(User user, Order order) {
        logger.info("Pharmacist {} is processing order #{}", user.getLogin(), order.getId());
        // Фармацевт обрабатывает заказы: проверяет, комплектует, меняет статус
    }
    
    @Override
    public void managePrescriptions(User user, Prescription prescription) {
        logger.info("Pharmacist {} is validating prescription #{} for medicine {}",
                   user.getLogin(), prescription.getId(), prescription.getMedicine().getName());
        // Фармацевт проверяет рецепты при отпуске лекарств
    }
    
    @Override
    public void manageMedicineCatalog(User user, Medicine medicine) {
        logger.info("Pharmacist {} is updating medicine #{}: price={}, stock={}",
                   user.getLogin(), medicine.getId(), medicine.getPrice(), medicine.getQuantityInStock());
        // Фармацевт управляет каталогом: обновляет цены, остатки, добавляет/удаляет
    }
    
    @Override
    public void manageUsers(User user, User targetUser) {
        throw new UnsupportedOperationException("Pharmacist cannot manage users");
    }
    
    @Override
    public void requestPrescription(User user, PrescriptionRequestDto requestDto) {
        throw new UnsupportedOperationException("Pharmacist cannot request prescriptions");
    }
    
    @Override
    public String getRoleName() {
        return "PHARMACIST";
    }
    
    @Override
    public boolean hasPermission(String action) {
        return switch (action) {
            case "VIEW_CATALOG", "MANAGE_ORDERS", 
                 "VALIDATE_PRESCRIPTIONS", "MANAGE_MEDICINE_CATALOG" -> true;
            default -> false;
        };
    }
    
    @Override
    public String toString() {
        return "PharmacistRole";
    }
}