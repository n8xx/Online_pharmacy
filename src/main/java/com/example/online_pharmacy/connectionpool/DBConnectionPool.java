package com.example.online_pharmacy.connectionpool;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class DBConnectionPool {
    private static final Logger logger = LogManager.getLogger();
    private static final String PROPERTY_PATH = "database.properties";

    private ArrayBlockingQueue<Connection> freeConnections;
    private ArrayBlockingQueue<Connection> takenConnections;

    private static final ReentrantLock lock = new ReentrantLock();
    private static volatile DBConnectionPool instance;

    public static DBConnectionPool getInstance() {
        if (instance == null) {
            try {
                lock.lock();
                if (instance == null) {
                    instance = new DBConnectionPool();
                }
            } catch (Exception e) {
                logger.fatal("Cannot get instance of MySQL connection pool", e);
                throw new RuntimeException("Failed to initialize connection pool", e);
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    private DBConnectionPool() {
        try {
            lock.lock();
            if (instance != null) {
                throw new UnsupportedOperationException("Use getInstance() instead");
            } else {
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                init();
            }
        } catch (SQLException e) {
            logger.fatal("Cannot initialize MySQL connection pool", e);
            throw new RuntimeException("Failed to initialize connection pool", e);
        } finally {
            lock.unlock();
        }
    }

    private void init() {
        ResourceBundle resource = ResourceBundle.getBundle(PROPERTY_PATH, Locale.getDefault());
        if (resource == null) {
            logger.fatal("Error while reading database properties");
            throw new RuntimeException("Database properties file not found");
        }

        try {
            String connectionURL = resource.getString("db.url");
            String initialCapacityString = resource.getString("db.pool.size");
            String user = resource.getString("db.user");
            String pass = resource.getString("db.password");

            int initialCapacity = Integer.parseInt(initialCapacityString);

            freeConnections = new ArrayBlockingQueue<>(initialCapacity);
            takenConnections = new ArrayBlockingQueue<>(initialCapacity);

            for (int i = 0; i < initialCapacity; i++) {
                try {
                    
                    Connection connection = DriverManager.getConnection(
                            connectionURL + "&useSSL=false&serverTimezone=UTC",
                            user,
                            pass
                    );
                    freeConnections.add(connection);
                } catch (SQLException e) {
                    logger.error("Failed to create connection #{}", i + 1, e);
                    throw e;
                }
            }
            logger.info("MySQL connection pool initialized with {} connections", initialCapacity);
        } catch (Exception e) {
            logger.fatal("Pool cannot be initialized with given parameters", e);
            throw new RuntimeException("Failed to initialize pool with parameters", e);
        }
    }

    public Connection getConnection() {
        try {
            Connection connection = freeConnections.take();
            takenConnections.offer(connection);
            logger.debug("Connection taken. Free connections: {}", freeConnections.size());
            return connection;
        } catch (InterruptedException e) {
            logger.error("Could not get a connection to the database", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get connection", e);
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection == null) {
            logger.warn("Attempted to release null connection");
            return;
        }

        if (takenConnections.remove(connection)) {
            try {
                if (!connection.isClosed()) {
            
                    if (!connection.getAutoCommit()) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                    freeConnections.offer(connection);
                    logger.debug("Connection released. Free connections: {}", freeConnections.size());
                } else {
                   
                    replaceClosedConnection();
                }
            } catch (SQLException e) {
                logger.error("Error while releasing connection", e);
                replaceClosedConnection();
            }
        } else {
            logger.warn("Released connection was not in taken connections list");
        }
    }

    private void replaceClosedConnection() {
        try {
            ResourceBundle resource = ResourceBundle.getBundle(PROPERTY_PATH, Locale.getDefault());
            String connectionURL = resource.getString("db.url");
            String user = resource.getString("db.user");
            String pass = resource.getString("db.password");

            Connection newConnection = DriverManager.getConnection(
                    connectionURL + "&useSSL=false&serverTimezone=UTC",
                    user,
                    pass
            );
            freeConnections.offer(newConnection);
            logger.info("Replaced closed connection");
        } catch (Exception e) {
            logger.error("Failed to replace closed connection", e);
        }
    }

    public void destroy() {
        try {
        
            while (!freeConnections.isEmpty()) {
                Connection connection = freeConnections.poll();
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            }

            while (!takenConnections.isEmpty()) {
                Connection connection = takenConnections.poll();
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            }

            logger.info("All connections closed");

            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (driver.getClass().getName().contains("mysql")) {
                    DriverManager.deregisterDriver(driver);
                }
            }
            logger.info("MySQL driver unregistered");
        } catch (SQLException e) {
            logger.error("Couldn't close connections while destroying the pool", e);
        }
    }

    public int getFreeConnectionsCount() {
        return freeConnections.size();
    }

    public int getTakenConnectionsCount() {
        return takenConnections.size();
    }

    public int getTotalCapacity() {
        return freeConnections.size() + takenConnections.size();
    }
}
