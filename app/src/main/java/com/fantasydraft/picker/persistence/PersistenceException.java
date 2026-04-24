package com.fantasydraft.picker.persistence;

/**
 * Exception thrown when persistence operations fail.
 */
public class PersistenceException extends Exception {
    
    public enum ErrorType {
        STORAGE_FULL,
        CORRUPTED_DATA,
        DATABASE_ERROR,
        UNKNOWN
    }
    
    private final ErrorType errorType;
    
    public PersistenceException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    public PersistenceException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
}
