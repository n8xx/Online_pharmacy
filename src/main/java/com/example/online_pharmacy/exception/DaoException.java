package com.example.online_pharmacy.exception;

public class DaoException extends Exception {

    public DaoException() {
        super();
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable reason) {
        super(reason);
    }

    public DaoException(String message, Throwable reason) {
        super(message, reason);
    }

}