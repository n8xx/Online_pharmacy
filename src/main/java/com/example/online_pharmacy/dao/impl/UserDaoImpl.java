package com.example.online_pharmacy.dao.impl;

import com.example.online_pharmacy.connectionpool.DBConnectionPool;
import com.example.online_pharmacy.dao.UserDao;
import com.example.online_pharmacy.exception.DaoException;
import com.example.online_pharmacy.model.Role;
import com.example.online_pharmacy.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static UserDaoImpl instance;
    private UserDaoImpl() {}

    public static UserDaoImpl getInstance() {
        if (instance == null) {
            instance = new UserDaoImpl();
        }
        return instance;
    }

    private final DBConnectionPool connectionPool = DBConnectionPool.getInstance();

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM users WHERE id = ? AND is_active = TRUE";

    private static final String FIND_BY_LOGIN_SQL =
            "SELECT * FROM users WHERE login = ? AND is_active = TRUE";

    private static final String FIND_BY_ROLE_SQL =
            "SELECT * FROM users WHERE role = ? AND is_active = TRUE ORDER BY created_at DESC";

    private static final String FIND_BY_EMAIL_SQL =
            "SELECT * FROM users WHERE email = ? AND is_active = TRUE";

    private static final String INSERT_USER_SQL =
            "INSERT INTO users (login, password_hash, email, first_name, last_name, " +
                    "phone_number, role, is_active, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?,)";

    private static final String UPDATE_USER_SQL =
            "UPDATE users SET email = ?, first_name = ?, last_name = ?, " +
                    "phone_number = ?, role = ?, is_active = ?, last_login_at = ? " +
                    "WHERE id = ?";

    private static final String SOFT_DELETE_USER_SQL =
            "UPDATE users SET is_active = FALSE WHERE id = ?";

    private static final String UPDATE_LAST_LOGIN_SQL =
            "UPDATE users SET last_login_at = ? WHERE id = ?";

    @Override
    public Optional<User> findById(Long id) throws DaoException {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error finding user by id: " + id, e);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) throws DaoException {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_LOGIN_SQL)) {

            ps.setString(1, login.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error finding user by login: " + login, e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws DaoException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {

            ps.setString(1, email.trim().toLowerCase());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DaoException("Error finding user by email: " + email, e);
        }
    }

    @Override
    public List<User> findByRole(Role role) throws DaoException {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        List<User> users = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_BY_ROLE_SQL)) {

            ps.setString(1, role.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;

        } catch (SQLException e) {
            throw new DaoException("Error finding users by role: " + role, e);
        }
    }

    @Override
    public User save(User user) throws DaoException {
        if (user == null) {
            throw new DaoException("User cannot be null");
        }

        if (user.getId() == 0) {
            return insertUser(user);
        } else {
            return user;
        }
    }

    private User insertUser(User user) throws DaoException {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getRole().name());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Creating user failed, no rows affected");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("Creating user failed, no ID obtained");
                }
            }

            return user;

        } catch (SQLException e) {
            throw new DaoException("Error inserting user: " + user.getLogin(), e);
        }
    }

    @Override
    public boolean update(User user) throws DaoException {
        if (user == null || user.getId() == 0) {
            throw new DaoException("User must have valid ID for update");
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_USER_SQL)) {

            ps.setString(1, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getRole().name());

            if (user.getLastLoginAt() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(user.getLastLoginAt()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            ps.setLong(8, user.getId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DaoException("Error updating user with id: " + user.getId(), e);
        }
    }

    @Override
    public boolean delete(Long id) throws DaoException {
        if (id == null || id <= 0) {
            throw new DaoException("Invalid user ID for deletion");
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(SOFT_DELETE_USER_SQL)) {

            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DaoException("Error deleting user with id: " + id, e);
        }
    }


    public boolean updateLastLogin(Long userId) throws DaoException {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_LAST_LOGIN_SQL)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DaoException("Error updating last login for user id: " + userId, e);
        }
    }

    public boolean existsByLogin(String login) throws DaoException {
        return findByLogin(login).isPresent();
    }

    public boolean existsByEmail(String email) throws DaoException {
        return findByEmail(email).isPresent();
    }

    public List<User> findAllActive() throws DaoException {
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;

        } catch (SQLException e) {
            throw new DaoException("Error finding all active users", e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp lastLogin = rs.getTimestamp("last_login_at");
        if (lastLogin != null && !rs.wasNull()) {
            user.setLastLoginAt(lastLogin.toLocalDateTime());
        }

        return user;
    }

    private static void validateUserForSave(User user) throws DaoException {
        if (user == null) {
            throw new DaoException("User cannot be null");
        }
        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            throw new DaoException("User login cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new DaoException("User email cannot be empty");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new DaoException("User password hash cannot be empty");
        }
        if (user.getRole() == null) {
            throw new DaoException("User role cannot be null");
        }
    }
}