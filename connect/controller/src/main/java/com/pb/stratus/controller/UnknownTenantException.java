package com.pb.stratus.controller;

public class UnknownTenantException extends RuntimeException {
    public UnknownTenantException() {
    }

    public UnknownTenantException(String message) {
        super(message);
    }

    public UnknownTenantException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownTenantException(Throwable cause) {
        super(cause);
    }
}
