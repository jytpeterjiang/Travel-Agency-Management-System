package tams.model;

/**
 * Enum representing the possible statuses of a booking.
 */
public enum BookingStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELED("Canceled"),
    CANCELLED("Cancelled"); // Alias for CANCELED with British spelling
    
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