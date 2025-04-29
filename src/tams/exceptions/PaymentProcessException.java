package tams.exceptions;

/**
 * Custom exception for handling errors that occur during payment processing.
 */
public class PaymentProcessException extends Exception {
    
    /**
     * Constructor with an error message.
     * 
     * @param message the error message
     */
    public PaymentProcessException(String message) {
        super(message);
    }
    
    /**
     * Constructor with an error message and cause.
     * 
     * @param message the error message
     * @param cause the cause of the exception
     */
    public PaymentProcessException(String message, Throwable cause) {
        super(message, cause);
    }
} 