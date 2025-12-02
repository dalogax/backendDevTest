package com.zara.similarproducts.domain.model;

public class ExternalServiceException extends DomainException {
    
    private static final String ERROR_CODE = "EXTERNAL_SERVICE_ERROR";
    
    public ExternalServiceException(String message) {
        super(message);
    }
    
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}