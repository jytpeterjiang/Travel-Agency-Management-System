package tams.model;

import java.util.Date;

/**
 * Represents a booking in the travel agency system.
 */
public class Booking {
    
    private String bookingId;
    private Customer customer;
    private TravelService service;
    private Date date;
    private BookingStatus status;
    private Payment payment;
    private int numTravelers;
    private String specialRequests;
    
    /**
     * Constructor for creating a new booking.
     * 
     * @param bookingId the unique identifier for this booking
     * @param customer the customer making the booking
     * @param service the travel service being booked
     */
    public Booking(String bookingId, Customer customer, TravelService service) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.service = service;
        this.date = new Date(); // Current date/time
        this.status = BookingStatus.PENDING;
    }
    
    /**
     * Overloaded constructor with payment.
     */
    public Booking(String bookingId, Customer customer, TravelService service, Payment payment) {
        this(bookingId, customer, service);
        this.payment = payment;
    }
    
    /**
     * Full constructor with all fields.
     */
    public Booking(String bookingId, Customer customer, TravelService service, 
                  Date date, BookingStatus status, int numTravelers, String specialRequests) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.service = service;
        this.date = date != null ? new Date(date.getTime()) : new Date();
        this.status = status != null ? status : BookingStatus.PENDING;
        this.numTravelers = numTravelers;
        this.specialRequests = specialRequests;
    }
    
    /**
     * Calculate the total price for this booking.
     * 
     * @return the total price as a double
     */
    public double calculateTotal() {
        return service.calculateTotalPrice();
    }
    
    /**
     * Update the status of this booking.
     * 
     * @param newStatus the new status
     */
    public void updateStatus(BookingStatus newStatus) {
        this.status = newStatus;
    }
    
    /**
     * Add a payment to this booking.
     * 
     * @param payment the payment to add
     */
    public void addPayment(Payment payment) {
        this.payment = payment;
    }
    
    /**
     * Process payment and confirm booking if payment is successful.
     * 
     * @return true if booking was confirmed, false otherwise
     */
    public boolean confirmBooking() {
        if (payment != null && payment.processPayment()) {
            status = BookingStatus.CONFIRMED;
            return true;
        }
        return false;
    }
    
    /**
     * Cancel this booking.
     * 
     * @return true if the booking was successfully canceled
     */
    public boolean cancelBooking() {
        // In a real system, we might check cancellation policy, apply fees, etc.
        if (status != BookingStatus.CANCELLED) {
            status = BookingStatus.CANCELLED;
            return true;
        }
        return false;
    }
    
    /**
     * Set the date of this booking.
     * 
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = new Date(date.getTime());
    }
    
    /**
     * Get the total price for this booking.
     * 
     * @return the total price
     */
    public double getTotalPrice() {
        return calculateTotal();
    }
    
    /**
     * Get the travel package associated with this booking.
     * This is a convenience method for getting the service as a TravelPackage.
     * 
     * @return the travel package, or null if service is not a TravelPackage
     */
    public TravelPackage getPackage() {
        if (service instanceof TravelPackage) {
            return (TravelPackage) service;
        }
        return null;
    }
    
    /**
     * Get the booking date.
     * 
     * @return the booking date
     */
    public Date getBookingDate() {
        return getDate();
    }
    
    /**
     * Get the number of travelers.
     * 
     * @return the number of travelers (always at least 1)
     */
    public int getNumTravelers() {
        // Ensure we always return at least 1 traveler
        return Math.max(1, numTravelers);
    }
    
    /**
     * Set the number of travelers.
     * 
     * @param numTravelers the number of travelers
     */
    public void setNumTravelers(int numTravelers) {
        this.numTravelers = numTravelers;
    }
    
    /**
     * Get any special requests for this booking.
     * 
     * @return the special requests
     */
    public String getSpecialRequests() {
        return specialRequests != null ? specialRequests : "";
    }
    
    /**
     * Set any special requests for this booking.
     * 
     * @param specialRequests the special requests
     */
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    // Getters and setters
    
    public String getBookingId() {
        return bookingId;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public TravelService getService() {
        return service;
    }
    
    public Date getDate() {
        return new Date(date.getTime());
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public Payment getPayment() {
        return payment;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Booking other = (Booking) obj;
        return bookingId.equals(other.bookingId);
    }
    
    @Override
    public int hashCode() {
        return bookingId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Booking #%s - %s - %s - Status: %s", 
            bookingId, customer.getName(), service.getName(), status.getDisplayName());
    }
} 