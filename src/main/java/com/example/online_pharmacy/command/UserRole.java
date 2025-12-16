package com.example.online_pharmacy.command;

import com.example.online_pharmacy.entity.User;
import com.example.online_pharmacy.entity.Medicine;
import com.example.online_pharmacy.entity.Order;
import com.example.online_pharmacy.entity.Prescription;


public interface UserRole {

    void viewMedicines(User user);

    void purchaseMedicine(User user, Medicine medicine, int quantity);

    void manageOrders(User user, Order order);


    void managePrescriptions(User user, Prescription prescription);

    void manageMedicineCatalog(User user, Medicine medicine);

    void manageUsers(User user, User targetUser);

    void requestPrescription(User user);

    static UserRole fromRole(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        switch (role.toUpperCase()) {
            case "CLIENT":
                return new ClientRole();
            case "DOCTOR":
                return new DoctorRole();
            case "PHARMACIST":
                return new PharmacistRole();
            case "ADMIN":
                return new AdminRole();
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}