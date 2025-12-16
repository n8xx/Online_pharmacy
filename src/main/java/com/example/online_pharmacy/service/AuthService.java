package com.example.online_pharmacy.service;

import com.example.online_pharmacy.model.User;
import com.example.online_pharmacy.exception.ServiceException;

public interface AuthService {
    

    User authenticate(String username, String password) throws ServiceException;

    boolean register(User user, String password) throws ServiceException;
    
    void updateLastLogin(Long userId) throws ServiceException;

    boolean userExists(String username, String email) throws ServiceException;
}