package tams.model;

import java.util.ArrayList;

/**
 * Represents a customer in the travel agency system.
 */
public class Customer {
    
    private String customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private ArrayList<Booking> bookings;
    
    /**
     * Constructor for creating a new customer.
     * 
     * @param customerId the unique identifier for this customer
     * @param name the name of this customer
     * @param email the email of this customer
     * @param phone the phone number of this customer
     * @param address the address of this customer
     */
    public Customer(String customerId, String name, String email, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.bookings = new ArrayList<>();
    }
    
    /**
     * Add a booking to this customer's booking history.
     * 
     * @param booking the booking to add
     */
    public void addBooking(Booking booking) {
        if (!bookings.contains(booking)) {
            bookings.add(booking);
        }
    }
    
    /**
     * Get the booking history for this customer.
     * 
     * @return an ArrayList containing all bookings
     */
    public ArrayList<Booking> getBookingHistory() {
        return new ArrayList<>(bookings);
    }
    
    // Getters and setters
    
    public String getCustomerId() {
        return customerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Customer other = (Customer) obj;
        return customerId.equals(other.customerId);
    }
    
    @Override
    public int hashCode() {
        return customerId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", name, email);
    }
} 