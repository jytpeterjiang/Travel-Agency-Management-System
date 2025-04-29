package tams.model.interfaces;

/**
 * Interface defining methods that any bookable travel service must implement.
 */
public interface Bookable {
    
    /**
     * Calculate the price of the bookable service.
     * 
     * @return the calculated price as a double
     */
    double calculatePrice();
    
    /**
     * Check if the service is available for booking.
     * 
     * @return true if available, false otherwise
     */
    boolean checkAvailability();
    
    /**
     * Make a reservation for this service.
     * 
     * @return true if reservation was successful, false otherwise
     */
    boolean makeReservation();
} 