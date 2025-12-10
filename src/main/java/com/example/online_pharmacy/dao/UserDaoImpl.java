package com.example.online_pharmacy.dao;

import com.example.online_pharmacy.connectionpool.DBConnectionPool;
import com.example.online_pharmacy.exception.DaoException;
import com.example.online_pharmacy.model.Role;
import com.example.online_pharmacy.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        DBConnectionPool connectionPool;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding user by id: " + id, e);
        }
        return Optional.empty();
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
