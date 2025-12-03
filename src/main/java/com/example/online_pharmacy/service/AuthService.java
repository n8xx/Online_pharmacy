package com.example.online_pharmacy.service;

import com.example.online_pharmacy.entity.User;
import com.example.online_pharmacy.exception.ServiceException;

public interface AuthService {
    
    /**
     * Аутентификация пользователя
     */
    User authenticate(String username, String password) throws ServiceException;
    
    /**
     * Регистрация нового пользователя
     */
    boolean register(User user, String password) throws ServiceException;
    
    /**
     * Обновление времени последнего входа
     */
    void updateLastLogin(Long userId) throws ServiceException;
    
    /**
     * Проверка существования пользователя
     */
    boolean userExists(String username, String email) throws ServiceException;
}