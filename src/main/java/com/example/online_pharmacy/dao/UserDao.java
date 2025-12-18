package com.example.online_pharmacy.dao;

import com.example.online_pharmacy.exception.DaoException;
import com.example.online_pharmacy.model.Role;
import com.example.online_pharmacy.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id) throws DaoException;
    Optional<User> findByLogin(String login) throws DaoException;
    Optional<User> findByEmail(String email) throws DaoException ;
    List<User> findByRole(Role role) throws DaoException;
    User save(User user) throws DaoException;
    boolean update(User user)throws DaoException;
    boolean delete(Long id)throws DaoException;
}

