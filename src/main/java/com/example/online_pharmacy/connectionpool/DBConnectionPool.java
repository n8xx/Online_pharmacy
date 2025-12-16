package com.example.online_pharmacy.connectionpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class DBConnectionPool {
    private static final Logger logger =  LogManager.getLogger();
    private static DBConnectionPool instance;
    
    private static final ReentrantLock creationLock = new ReentrantLock();
    
    private final ArrayBlockingQueue<Connection> freeConnections;
    private final ArrayBlockingQueue<Connection> takenConnections;
    private final int poolSize;

    
    private DBConnectionPool(int poolSize) {
        this.poolSize = poolSize;
        this.freeConnections = new ArrayBlockingQueue<>(poolSize);
        this.takenConnections = new ArrayBlockingQueue<>(poolSize);
        initConnections();
    }

 
    public static DBConnectionPool getInstance() {
        if (instance == null) {
            creationLock.lock(); 
            try {
                if (instance == null) {
                    instance = createInstance();
                }
            } finally {
                creationLock.unlock();
            }
        }
        return instance;
    }
    
    private static DBConnectionPool createInstance() {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("database");
            int poolSize = Integer.parseInt(rb.getString("db.pool.size"));
            return new DBConnectionPool(poolSize);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create connection pool", e);
        }
    }
    
    private void initConnections() {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("database");
            String url = rb.getString("db.url");
            String user = rb.getString("db.user");
            String password = rb.getString("db.password");
            
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            
            for (int i = 0; i < poolSize; i++) {
                Connection conn = DriverManager.getConnection(url, user, password);
                freeConnections.offer(conn);
            }

            logger.info("Connection pool initialized with " + poolSize + " connections");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize connections", e);
        }
    }
    
    private final ReentrantLock poolOperationLock = new ReentrantLock();
    
    public Connection getConnection() {
        try {
            Connection conn = freeConnections.take();

            poolOperationLock.lock();
            try {
                takenConnections.offer(conn);
                logger.info("Connection taken. Free: " + freeConnections.size() +
                        ", Taken: " + takenConnections.size());
            } finally {
                poolOperationLock.unlock();
            }

            return conn;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for connection", e);
        }
    }
    
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            logger.info("Warning: Attempted to release null connection");
            return;
        }

        poolOperationLock.lock();
        try {
            boolean wasTaken = takenConnections.remove(connection);

            if (wasTaken) {
                try {
                    if (!connection.isClosed()) {
                        if (!connection.getAutoCommit()) {
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }

                        freeConnections.offer(connection);
                        logger.info("Connection released. Free: " + freeConnections.size());

                    } else {
                        replaceClosedConnection();
                    }

                } catch (SQLException e) {
                    logger.info("Error while releasing connection: " + e.getMessage());
                    replaceClosedConnection();
                }
            } else {
                logger.info("Warning: Released connection was not in taken pool");
            }
        } finally {
            poolOperationLock.unlock();
        }
    }
    
    private void replaceClosedConnection() {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("database");
            String url = rb.getString("db.url");
            String user = rb.getString("db.user");
            String password = rb.getString("db.password");

            Connection newConn = DriverManager.getConnection(url, user, password);

            poolOperationLock.lock();
            try {
                freeConnections.offer(newConn);
                logger.info("Replaced closed connection");
            } finally {
                poolOperationLock.unlock();
            }

        } catch (Exception e) {
            logger.info("Failed to replace closed connection: " + e.getMessage());
        }
    }
    
    public void destroy() {
        logger.info("Destroying connection pool...");

        poolOperationLock.lock();
        try {
            closeAllConnectionsInQueue(freeConnections);
            closeAllConnectionsInQueue(takenConnections);

            creationLock.lock();
            try {
                instance = null;
            } finally {
                creationLock.unlock();
            }

        } finally {
            poolOperationLock.unlock();
        }

        logger.info("Connection pool destroyed");
    }
    
    private void closeAllConnectionsInQueue(ArrayBlockingQueue<Connection> queue) {
        Connection conn;
        while ((conn = queue.poll()) != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.info("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    public int getFreeConnectionsCount() {
        poolOperationLock.lock();
        try {
            return freeConnections.size();
        } finally {
            poolOperationLock.unlock();
        }
    }

    public int getTakenConnectionsCount() {
        poolOperationLock.lock();
        try {
            return takenConnections.size();
        } finally {
            poolOperationLock.unlock();
        }
    }
    
    public void printLockStatus() {
        logger.info("Creation lock: locked=" + creationLock.isLocked() +
                ", held by current thread=" + creationLock.isHeldByCurrentThread() +
                ", queue length=" + creationLock.getQueueLength());

        logger.info("Pool operation lock: locked=" + poolOperationLock.isLocked() +
                ", held by current thread=" + poolOperationLock.isHeldByCurrentThread() +
                ", queue length=" + poolOperationLock.getQueueLength());
    }

}