package com.example.online_pharmacy.command.impl;

import com.example.online_pharmacy.command.UserRole;
import com.example.online_pharmacy.entity.User;
import com.example.online_pharmacy.entity.Medicine;
import com.example.online_pharmacy.entity.Order;
import com.example.online_pharmacy.entity.Prescription;
import com.example.online_pharmacy.service.PrescriptionService;
import com.example.online_pharmacy.service.impl.PrescriptionServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;


public class DoctorRole implements UserRole {
    private static final Logger logger = LogManager.getLogger();
    private final PrescriptionService prescriptionService = PrescriptionServiceImpl.getInstance();
    private final UserService userService = UserServiceImpl.getInstance();
    
    @Override
    public void viewMedicines(User user) {
        logger.info("Doctor {} is viewing medicine catalog for prescription purposes", 
                   user.getLogin());
        // Врач видит все лекарства с информацией о необходимости рецепта
        System.out.println("Doctor view: Showing all medicines with prescription requirements");
    }
    
    @Override
    public void purchaseMedicine(User user, Medicine medicine, int quantity) {
        throw new UnsupportedOperationException("Doctor cannot purchase medicines - this is for patients only");
    }
    
    @Override
    public void manageOrders(User user, Order order) {
        throw new UnsupportedOperationException("Doctor cannot manage orders - contact pharmacist");
    }
    
    @Override
    public void managePrescriptions(User user, Prescription prescription) {
        logger.info("Doctor {} is managing prescription #{} for patient {}",
                   user.getLogin(), prescription.getId(), 
                   prescription.getClient().getFullName());
        
        // Врач может:
        // 1. Назначать новые рецепты
        // 2. Продлевать существующие
        // 3. Отменять рецепты
        // 4. Изменять дозировки
        
        System.out.println("Managing prescription for: " + prescription.getMedicine().getName());
        System.out.println("Patient: " + prescription.getClient().getFullName());
        System.out.println("Current status: " + prescription.getStatus());
    }
    
    /**
     * Назначение нового рецепта пациенту
     */
    public Prescription prescribeMedicine(User doctor, User patient, Medicine medicine, 
                                          int validDays, String diagnosis, String instructions) {
        logger.info("Doctor {} prescribing medicine {} to patient {}",
                   doctor.getLogin(), medicine.getName(), patient.getFullName());
        
        if (!"CLIENT".equals(patient.getRole().name())) {
            throw new IllegalArgumentException("Can only prescribe to clients/patients");
        }
        
        if (!medicine.isPrescriptionRequired()) {
            logger.warn("Doctor {} attempted to prescribe non-prescription medicine {}",
                       doctor.getLogin(), medicine.getName());
            throw new IllegalArgumentException("This medicine does not require prescription");
        }
        
        Prescription prescription = new Prescription();
        prescription.setClient(patient);
        prescription.setDoctor(doctor);
        prescription.setMedicine(medicine);
        prescription.setIssueDate(LocalDateTime.now());
        prescription.setExpiryDate(LocalDateTime.now().plusDays(validDays));
        prescription.setDiagnosis(diagnosis);
        prescription.setDosageInstructions(instructions);
        prescription.setStatus(PrescriptionStatus.ACTIVE);
        prescription.setRefillsRemaining(3); // По умолчанию 3 повторения
        
        // Сохраняем в БД через сервис
        prescriptionService.create(prescription);
        
        logger.info("Prescription #{} created successfully", prescription.getId());
        return prescription;
    }
    
    /**
     * Продление срока действия рецепта
     */
    public void extendPrescription(User doctor, Prescription prescription, int additionalDays) {
        logger.info("Doctor {} extending prescription #{} by {} days",
                   doctor.getLogin(), prescription.getId(), additionalDays);
        
        if (!prescription.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("You can only extend your own prescriptions");
        }
        
        if (!prescription.isValid()) {
            throw new IllegalStateException("Cannot extend invalid/expired prescription");
        }
        
        prescription.extend(additionalDays);
        prescriptionService.update(prescription);
        
        logger.info("Prescription #{} extended until {}", 
                   prescription.getId(), prescription.getExpiryDate());
    }
    
    /**
     * Отмена рецепта
     */
    public void cancelPrescription(User doctor, Prescription prescription, String reason) {
        logger.info("Doctor {} cancelling prescription #{} for patient {}. Reason: {}",
                   doctor.getLogin(), prescription.getId(), 
                   prescription.getClient().getFullName(), reason);
        
        if (!prescription.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("You can only cancel your own prescriptions");
        }
        
        prescription.setStatus(PrescriptionStatus.REJECTED);
        prescriptionService.update(prescription);
        
        logger.warn("Prescription #{} cancelled by doctor {}. Reason: {}",
                   prescription.getId(), doctor.getLogin(), reason);
    }
    
    @Override
    public void manageMedicineCatalog(User user, Medicine medicine) {
        throw new UnsupportedOperationException("Doctor cannot manage medicine catalog - contact pharmacist");
    }
    
    @Override
    public void manageUsers(User user, User targetUser) {
        logger.info("Doctor {} viewing patient {}", user.getLogin(), targetUser.getFullName());
        
        // Врач может просматривать только пациентов (клиентов)
        if (!"CLIENT".equals(targetUser.getRole().name())) {
            throw new SecurityException("Doctors can only view client/patient profiles");
        }
        
        // Получаем историю рецептов пациента
        List<Prescription> patientPrescriptions = prescriptionService.findByClientId(targetUser.getId());
        
        System.out.println("=== Patient Medical Profile ===");
        System.out.println("Name: " + targetUser.getFullName());
        System.out.println("Email: " + targetUser.getEmail());
        System.out.println("Phone: " + targetUser.getPhoneNumber());
        System.out.println("Active prescriptions: " + 
            patientPrescriptions.stream().filter(Prescription::isValid).count());
        System.out.println("Total prescriptions: " + patientPrescriptions.size());
    }
    
    @Override
    public void requestPrescription(User user, PrescriptionRequestDto requestDto) {
        throw new UnsupportedOperationException("Doctor cannot request prescriptions - patients request, doctors approve");
    }
    
    /**
     * Просмотр запросов на рецепты от пациентов
     */
    public List<PrescriptionRequestDto> viewPrescriptionRequests(User doctor) {
        logger.info("Doctor {} viewing pending prescription requests", doctor.getLogin());
        
        // Получаем все ожидающие запросы
        List<PrescriptionRequestDto> requests = prescriptionService.findPendingRequests();
        
        System.out.println("=== Pending Prescription Requests ===");
        requests.forEach(request -> {
            System.out.println("Request ID: " + request.getId());
            System.out.println("Patient: " + request.getClientName());
            System.out.println("Medicine: " + request.getMedicineName());
            System.out.println("Note: " + request.getClientNote());
            System.out.println("---");
        });
        
        return requests;
    }
    
    /**
     * Обработка запроса на рецепт
     */
    public void processPrescriptionRequest(User doctor, Long requestId, boolean approve, 
                                          String response, int validDays) {
        logger.info("Doctor {} processing request #{}. Decision: {}",
                   doctor.getLogin(), requestId, approve ? "APPROVE" : "REJECT");
        
        prescriptionService.processRequest(requestId, doctor.getId(), approve, response, validDays);
        
        if (approve) {
            logger.info("Prescription request #{} approved by doctor {}", 
                       requestId, doctor.getLogin());
        } else {
            logger.info("Prescription request #{} rejected by doctor {}", 
                       requestId, doctor.getLogin());
        }
    }
    
    @Override
    public String getRoleName() {
        return "DOCTOR";
    }
    
    @Override
    public boolean hasPermission(String action) {
        return switch (action) {
            case "VIEW_CATALOG", 
                 "MANAGE_PRESCRIPTIONS", 
                 "VIEW_PATIENTS", 
                 "VIEW_PRESCRIPTION_REQUESTS",
                 "PRESCRIBE_MEDICINE",
                 "EXTEND_PRESCRIPTION",
                 "CANCEL_PRESCRIPTION",
                 "PROCESS_PRESCRIPTION_REQUEST" -> true;
            default -> false;
        };
    }
    
    /**
     * Получение списка пациентов врача
     */
    public List<User> getMyPatients(User doctor) {
        logger.debug("Doctor {} retrieving patient list", doctor.getLogin());
        
        // Получаем всех клиентов, которым врач назначал рецепты
        List<Prescription> myPrescriptions = prescriptionService.findByDoctorId(doctor.getId());
        
        return myPrescriptions.stream()
            .map(Prescription::getClient)
            .distinct()
            .toList();
    }
    
    /**
     * Просмотр истории рецептов пациента
     */
    public List<Prescription> getPatientHistory(User doctor, Long patientId) {
        logger.info("Doctor {} viewing prescription history for patient #{}",
                   doctor.getLogin(), patientId);
        
        // Проверяем, что это действительно пациент этого врача
        User patient = userService.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
            
        if (!"CLIENT".equals(patient.getRole().name())) {
            throw new SecurityException("Can only view history of clients/patients");
        }
        
        return prescriptionService.findByClientId(patientId).stream()
            .filter(p -> p.getDoctor().getId().equals(doctor.getId()))
            .toList();
    }
    
    @Override
    public String toString() {
        return "DoctorRole";
    }
}