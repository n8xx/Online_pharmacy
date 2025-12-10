package com.example.online_pharmacy.dao;

import com.example.online_pharmacy.exception.DaoException;
import com.example.online_pharmacy.model.Role;
import com.example.online_pharmacy.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id) throws DaoException;
    Optional<User> findByLogin(String login);
    List<User> findByRole(Role role);
    User save(User user);
    boolean update(User user);
    boolean delete(Long id);
}

