package com.example.online_pharmacy.service.impl;

import com.example.online_pharmacy.dao.UserDao;
import com.example.online_pharmacy.dao.impl.UserDaoImpl;
import com.example.online_pharmacy.model.User;
import com.example.online_pharmacy.model.Role;
import com.example.online_pharmacy.exception.ServiceException;
import com.example.online_pharmacy.service.AuthService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.time.LocalDateTime;
import java.util.Optional;


public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LogManager.getLogger();
    private static volatile AuthServiceImpl instance;


    private final UserDao userDao = UserDaoImpl.getInstance();

    private AuthServiceImpl() {}
    
    public static AuthServiceImpl getInstance() {
        if (instance == null) {
            synchronized (AuthServiceImpl.class) {
                if (instance == null) {
                    instance = new AuthServiceImpl();
                }
            }
        }
        return instance;
    }
    
    @Override
    public User authenticate(String username, String password) throws ServiceException {
        try {
            Optional<User> userOptional = userDao.findByLogin(username);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                if (password.equals(user.getPasswordHash())) {
                    return user;
                }
            }
            
            return null;
            
        } catch (Exception e) {
            logger.error("Authentication error for user: "+ username, e);
            throw new ServiceException("Authentication failed", e);
        }
    }
    
    @Override
    public boolean register(User user, String password) throws ServiceException {
        try {
            if (userDao.findByLogin(user.getLogin()).isPresent()) {
                return false;
            }
            

            user.setRole(Role.CLIENT);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            

            user.setPassword(password);

            User savedUser = userDao.save(user);
            
            return savedUser != null && savedUser.getId() != null;
            
        } catch (Exception e) {
            logger.info("Registration error for user: "+user.getLogin(), e);
            throw new ServiceException("Registration failed", e);
        }
    }
    
    @Override
    public void updateLastLogin(Long userId) throws ServiceException {
        try {
            Optional<User> userOptional = userDao.findById(userId);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setLastLoginAt(LocalDateTime.now());
                userDao.update(user);
            }
            
        } catch (Exception e) {
            logger.info("Error updating last login for user: "+ userId, e);
            throw new ServiceException("Failed to update last login", e);
        }
    }
    
    @Override
    public boolean userExists(String username, String email) throws ServiceException {
        try {
            return userDao.findByLogin(username).isPresent();
        } catch (Exception e) {
            logger.error("Error checking user existence", e);
            throw new ServiceException("Failed to check user existence", e);
        }
    }
}