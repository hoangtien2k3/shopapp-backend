package com.hoangtien2k3.shopappbackend.exceptions.payload;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
