package com.example.online_pharmacy.controller;

import com.example.online_pharmacy.entity.User;
import com.example.online_pharmacy.exception.ServiceException;
import com.example.online_pharmacy.service.AuthService;
import com.example.online_pharmacy.service.impl.AuthServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet("/auth")
public class AuthController extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(AuthController.class);
    
    private static final String LOGIN_ACTION = "login";
    private static final String REGISTER_ACTION = "register";
    private static final String LOGOUT_ACTION = "logout";
    private static final String HOME_ACTION = "home";
    private static final String ACTION_PARAMETER = "action";
    
    private static final String LOGIN_PAGE = "/WEB-INF/view/auth/login.jsp";
    private static final String REGISTER_PAGE = "/WEB-INF/view/auth/register.jsp";
    private static final String HOME_PAGE = "/WEB-INF/view/home.jsp";
    
    private static final String REDIRECT_TO_HOME = "/auth?action=home";
    private static final String REDIRECT_TO_LOGIN = "/auth?action=login";
    
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String SUCCESS_ATTRIBUTE = "success";
    private static final String USER_ATTRIBUTE = "user";
    
    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String EMAIL_PARAMETER = "email";
    private static final String FIRST_NAME_PARAMETER = "firstName";
    private static final String LAST_NAME_PARAMETER = "lastName";
    
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        authService = AuthServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String action = req.getParameter(ACTION_PARAMETER);
        String view = HOME_PAGE;

        switch (action) {
            case LOGIN_ACTION:
                view = LOGIN_PAGE;
                break;
            case REGISTER_ACTION:
                view = REGISTER_PAGE;
                break;
            case HOME_ACTION:
                view = HOME_PAGE;
                break;
            case LOGOUT_ACTION:
                req.getSession().invalidate();
                resp.sendRedirect(req.getContextPath() + REDIRECT_TO_LOGIN);
                return;
            default:
                if (req.getSession().getAttribute(USER_ATTRIBUTE) != null) {
                    view = HOME_PAGE;
                } else {
                    resp.sendRedirect(req.getContextPath() + REDIRECT_TO_LOGIN);
                    return;
                }
        }

        req.getRequestDispatcher(view).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String action = req.getParameter(ACTION_PARAMETER);
        
        switch (action) {
            case LOGIN_ACTION:
                handleLogin(req, resp);
                break;
            case REGISTER_ACTION:
                handleRegistration(req, resp);
                break;
        }
    }
    
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String username = req.getParameter(USERNAME_PARAMETER);
        String password = req.getParameter(PASSWORD_PARAMETER);
        
        try {
            User user = authService.authenticate(username, password);
            
            if (user != null && user.isActive()) {
                req.getSession().setAttribute(USER_ATTRIBUTE, user);
                logger.info("User {} logged in successfully", username);
                
                // Перенаправляем на главную
                resp.sendRedirect(req.getContextPath() + REDIRECT_TO_HOME);
            } else {
                req.setAttribute(ERROR_ATTRIBUTE, "Invalid username or password");
                req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
            }
            
        } catch (ServiceException e) {
            logger.error("Login error for user: {}", username, e);
            req.setAttribute(ERROR_ATTRIBUTE, "Authentication failed: " + e.getMessage());
            req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
        }
    }
    
    private void handleRegistration(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String username = req.getParameter(USERNAME_PARAMETER);
        String password = req.getParameter(PASSWORD_PARAMETER);
        String email = req.getParameter(EMAIL_PARAMETER);
        String firstName = req.getParameter(FIRST_NAME_PARAMETER);
        String lastName = req.getParameter(LAST_NAME_PARAMETER);
        
        try {
            User newUser = new User();
            newUser.setLogin(username);
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);

            
            boolean success = authService.register(newUser, password);
            
            if (success) {
                req.setAttribute(SUCCESS_ATTRIBUTE, "Registration successful! Please login.");
                req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
            } else {
                req.setAttribute(ERROR_ATTRIBUTE, "Registration failed. Username or email already exists.");
                req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
            }
            
        } catch (ServiceException e) {
            logger.error("Registration error for user: {}", username, e);
            req.setAttribute(ERROR_ATTRIBUTE, "Registration failed: " + e.getMessage());
            req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
        }
    }
}
