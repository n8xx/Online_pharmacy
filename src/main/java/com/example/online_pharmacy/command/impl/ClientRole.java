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
 * Реализация роли клиента.
 * Клиент может покупать лекарства, просматривать заказы, запрашивать рецепты.
 */
public class ClientRole implements UserRole {
    private static final Logger logger = LogManager.getLogger();
    
    @Override
    public void viewMedicines(User user) {
        logger.info("Client {} is browsing medicine catalog", user.getLogin());
        // Клиент видит каталог с отметками "требуется рецепт"
    }
    
    @Override
    public void purchaseMedicine(User user, Medicine medicine, int quantity) {
        if (medicine.isPrescriptionRequired()) {
            logger.warn("Client {} attempted to buy prescription medicine {} without prescription",
                       user.getLogin(), medicine.getName());
            throw new IllegalStateException("This medicine requires a valid prescription");
        }
        
        if (quantity > medicine.getQuantityInStock()) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        
        logger.info("Client {} purchased {} units of {} for ${}",
                   user.getLogin(), quantity, medicine.getName(), 
                   medicine.getPrice().multiply(BigDecimal.valueOf(quantity)));
        // Логика создания заказа
    }
    
    @Override
    public void manageOrders(User user, Order order) {
        // Клиент может только просматривать и отменять СВОИ заказы
        if (!order.getClient().getId().equals(user.getId())) {
            logger.error("Client {} attempted to access order #{} belonging to another user",
                        user.getLogin(), order.getId());
            throw new SecurityException("You can only manage your own orders");
        }
        
        logger.info("Client {} is viewing order #{} with status {}",
                   user.getLogin(), order.getId(), order.getStatus());
    }
    
    @Override
    public void managePrescriptions(User user, Prescription prescription) {
        // Клиент может только просматривать СВОИ рецепты
        if (!prescription.getClient().getId().equals(user.getId())) {
            throw new SecurityException("You can only view your own prescriptions");
        }
        
        logger.info("Client {} is checking prescription #{} for medicine {}",
                   user.getLogin(), prescription.getId(), prescription.getMedicine().getName());
    }
    
    @Override
    public void manageMedicineCatalog(User user, Medicine medicine) {
        throw new UnsupportedOperationException("Client cannot manage medicine catalog");
    }
    
    @Override
    public void manageUsers(User user, User targetUser) {
        throw new UnsupportedOperationException("Client cannot manage users");
    }
    
    @Override
    public void requestPrescription(User user, PrescriptionRequestDto requestDto) {
        logger.info("Client {} requested prescription for medicine #{} with note: {}",
                   user.getLogin(), requestDto.getMedicineId(), requestDto.getClientNote());
        // Логика создания запроса врачу на рецепт
    }
    
    @Override
    public String getRoleName() {
        return "CLIENT";
    }
    
    @Override
    public boolean hasPermission(String action) {
        return switch (action) {
            case "VIEW_CATALOG", "PURCHASE_MEDICINE", 
                 "VIEW_OWN_ORDERS", "VIEW_OWN_PRESCRIPTIONS",
                 "REQUEST_PRESCRIPTION", "CANCEL_ORDER" -> true;
            default -> false;
        };
    }
    
    @Override
    public String toString() {
        return "ClientRole";
    }
}