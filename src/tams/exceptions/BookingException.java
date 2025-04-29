package tams.exceptions;

/**
 * Custom exception for handling errors that occur during booking operations.
 */
public class BookingException extends Exception {
    
    /**
     * Constructor with an error message.
     * 
     * @param message the error message
     */
    public BookingException(String message) {
        super(message);
    }
    
    /**
     * Constructor with an error message and cause.
     * 
     * @param message the error message
     * @param cause the cause of the exception
     */
    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
} 