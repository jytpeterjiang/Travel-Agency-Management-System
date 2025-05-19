package tams.model;

/**
 * Enum representing the possible statuses of a booking.
 */
public enum BookingStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"); // Standard spelling used throughout the application
    
    private final String displayName;
    
    BookingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
} 