package com.example.online_pharmacy.exception;

public class ServiceException extends Exception {

        public ServiceException() {
            super();
        }
        public  ServiceException(String message) {
            super(message);
        }
        public ServiceException(Throwable reason) {
            super(reason);
        }
        public ServiceException(String message, Throwable reason) {
            super(message, reason);
        }
    }

