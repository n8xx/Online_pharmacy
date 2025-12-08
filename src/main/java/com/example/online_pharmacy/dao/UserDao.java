package com.example.online_pharmacy.dao;

import com.example.online_pharmacy.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id);
    Optional<User> findByLogin(String login);
    List<User> findByRole(Role role);
    User save(User user);
    boolean update(User user);
    boolean delete(Long id);
}
