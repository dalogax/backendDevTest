package com.zara.similarproducts.domain.model;

public abstract class DomainException extends RuntimeException {
    
    protected DomainException(String message) {
        super(message);
    }
    
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public abstract String getErrorCode();
}