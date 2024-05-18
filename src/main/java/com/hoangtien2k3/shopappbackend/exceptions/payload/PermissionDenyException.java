package com.hoangtien2k3.shopappbackend.exceptions.payload;

public class PermissionDenyException extends RuntimeException {
    public PermissionDenyException(String message) {
        super(message);
    }
}
