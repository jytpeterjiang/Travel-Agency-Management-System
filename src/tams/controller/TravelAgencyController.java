package tams.controller;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import tams.exceptions.BookingException;
import tams.exceptions.PaymentProcessException;
import tams.model.*;
import tams.model.interfaces.Bookable;
import tams.util.DataManager;

/**
 * Controller class that manages the business logic of the Travel Agency Management System.
 * Acts as an intermediary between the GUI and the data model.
 */
public class TravelAgencyController {
    
    private DataManager dataManager;
    
    /**
     * Constructor that initializes the controller with a data manager.
     */
    public TravelAgencyController() {
        dataManager = new DataManager();
        loadData();
    }
    
    /**
     * Load all data using the data manager.
     */
    public void loadData() {
        dataManager.loadData();
        
        // Add a sample review for testing if there are no reviews
        if (getReviews().isEmpty() && !getAllCustomers().isEmpty() && !getAllTravelPackages().isEmpty()) {
            createSampleReviews();
        }
    }
    
    /**
     * Create sample reviews for testing purposes.
     */
    private void createSampleReviews() {
        ArrayList<Customer> customers = getAllCustomers();
        ArrayList<TravelPackage> packages = getAllTravelPackages();
        
        if (!customers.isEmpty() && !packages.isEmpty()) {
            Customer customer = customers.get(0);
            TravelPackage travelPackage = packages.get(0);
            
            // Add a 5-star review
            addReview(travelPackage, customer, 5, "Excellent experience! The travel package exceeded my expectations. The accommodations were top-notch and the activities were well-organized.");
            
            // If there are more customers and packages, add more varied reviews
            if (customers.size() > 1 && packages.size() > 1) {
                addReview(packages.get(1), customers.get(1), 4, "Great package, but the weather wasn't ideal. Otherwise, the service was excellent.");
                
                if (packages.size() > 2) {
                    addReview(packages.get(2), customers.get(0), 3, "Average experience. Some activities were fun, but others were disappointing.");
                }
            }
        }
    }
    
    /**
     * Save all data using the data manager.
     */
    public void saveData() {
        dataManager.saveData();
    }
    
    /**
     * Create a new customer.
     * 
     * @param name customer name
     * @param email customer email
     * @param phone customer phone number
     * @return the created Customer object
     */
    public Customer createCustomer(String name, String email, String phone) {
        String customerId = "C" + UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer(customerId, name, email, phone, ""); // Default empty address
        dataManager.addCustomer(customer);
        return customer;
    }
    
    /**
     * Create a new customer with address.
     * 
     * @param name customer name
     * @param email customer email
     * @param phone customer phone number
     * @param address customer address
     * @return the created Customer object
     */
    public Customer createCustomer(String name, String email, String phone, String address) {
        String customerId = "C" + UUID.randomUUID().toString().substring(0, 8);
        Customer customer = new Customer(customerId, name, email, phone, address);
        dataManager.addCustomer(customer);
        return customer;
    }
    
    /**
     * Create a new travel package.
     * 
     * @param name package name
     * @param description package description
     * @param basePrice base price
     * @param destination destination
     * @param duration duration in days
     * @param accommodation accommodation details
     * @return the created TravelPackage object
     */
    public TravelPackage createTravelPackage(String name, String description, double basePrice, 
                                          String destination, int duration, String accommodation) {
        String packageId = "P" + UUID.randomUUID().toString().substring(0, 8);
        TravelPackage travelPackage = new TravelPackage(packageId, name, description, basePrice, 
                                                     destination, duration, accommodation);
        dataManager.addPackage(travelPackage);
        return travelPackage;
    }
    
    /**
     * Create a new activity.
     * 
     * @param name activity name
     * @param location activity location
     * @param duration duration in hours
     * @param cost cost
     * @return the created Activity object
     */
    public Activity createActivity(String name, String location, int duration, double cost) {
        String activityId = "A" + UUID.randomUUID().toString().substring(0, 8);
        Activity activity = new Activity(activityId, name, location, duration, cost);
        dataManager.addActivity(activity);
        return activity;
    }
    
    /**
     * Add an activity to a travel package.
     * 
     * @param travelPackage the travel package
     * @param activity the activity to add
     */
    public void addActivityToPackage(TravelPackage travelPackage, Activity activity) {
        travelPackage.addActivity(activity);
    }
    
    /**
     * Create a booking for a customer and a travel service.
     * 
     * @param customer the customer making the booking
     * @param service the travel service being booked
     * @return the created Booking
     * @throws BookingException if there's an issue with the booking
     */
    public Booking createBooking(Customer customer, TravelService service) throws BookingException {
        if (!(service instanceof Bookable)) {
            throw new BookingException("The selected service cannot be booked.");
        }
        
        Bookable bookableService = (Bookable) service;
        if (!bookableService.checkAvailability()) {
            throw new BookingException("The selected service is not available for booking.");
        }
        
        String bookingId = "B" + UUID.randomUUID().toString().substring(0, 8);
        Booking booking = new Booking(bookingId, customer, service);
        
        customer.addBooking(booking);
        dataManager.addBooking(booking);
        
        return booking;
    }
    
    /**
     * Create a booking with additional details.
     * 
     * @param customer the customer making the booking
     * @param travelPackage the travel package being booked
     * @param numTravelers number of travelers
     * @param status booking status
     * @param specialRequests any special requests
     * @return the created Booking
     * @throws BookingException if there's an issue with the booking
     */
    public Booking createBooking(Customer customer, TravelPackage travelPackage, int numTravelers, 
                                BookingStatus status, String specialRequests) throws BookingException {
        if (!travelPackage.checkAvailability()) {
            throw new BookingException("The selected package is not available for booking.");
        }
        
        String bookingId = "B" + UUID.randomUUID().toString().substring(0, 8);
        Date bookingDate = new Date(); // Current date/time
        
        Booking booking = new Booking(bookingId, customer, travelPackage, 
                                    bookingDate, status, numTravelers, specialRequests);
        
        customer.addBooking(booking);
        dataManager.addBooking(booking);
        
        return booking;
    }
    
    /**
     * Process payment for a booking.
     * 
     * @param booking the booking
     * @param amount the payment amount
     * @param method the payment method
     * @return true if payment was successful
     * @throws PaymentProcessException if there's an issue with payment processing
     */
    public boolean processPayment(Booking booking, double amount, PaymentMethod method) 
            throws PaymentProcessException {
        if (booking == null) {
            throw new PaymentProcessException("Invalid booking.");
        }
        
        if (amount <= 0) {
            throw new PaymentProcessException("Payment amount must be greater than zero.");
        }
        
        if (method == null) {
            throw new PaymentProcessException("Payment method must be specified.");
        }
        
        String paymentId = "PMT" + UUID.randomUUID().toString().substring(0, 8);
        Payment payment = new Payment(paymentId, amount, method);
        
        booking.addPayment(payment);
        
        if (payment.processPayment()) {
            booking.updateStatus(BookingStatus.CONFIRMED);
            return true;
        } else {
            throw new PaymentProcessException("Payment processing failed.");
        }
    }
    
    /**
     * Cancel a booking.
     * 
     * @param booking the booking to cancel
     * @return true if cancellation was successful
     * @throws BookingException if there's an issue with cancellation
     */
    public boolean cancelBooking(Booking booking) throws BookingException {
        if (booking == null) {
            throw new BookingException("Invalid booking.");
        }
        
        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new BookingException("Booking is already canceled.");
        }
        
        boolean success = booking.cancelBooking();
        
        if (success) {
            // If payment was already made, we might want to handle refund logic here
            if (booking.getPayment() != null && 
                booking.getPayment().getStatus() == PaymentStatus.COMPLETED) {
                booking.getPayment().setStatus(PaymentStatus.REFUNDED);
            }
        }
        
        return success;
    }
    
    /**
     * Add a review for a travel package.
     * 
     * @param travelPackage the travel package
     * @param customer the customer submitting the review
     * @param rating the rating (1-5)
     * @param comment the review comment
     * @return the created Review
     */
    public Review addReview(TravelPackage travelPackage, Customer customer, int rating, String comment) {
        String reviewId = "R" + UUID.randomUUID().toString().substring(0, 8);
        Review review = new Review(reviewId, customer, rating, comment);
        
        travelPackage.addReview(review);
        dataManager.addReview(review);
        
        // Save data immediately to persist the review
        saveData();
        
        return review;
    }
    
    /**
     * Get all reviews.
     * 
     * @return an ArrayList of all reviews
     */
    public ArrayList<Review> getReviews() {
        return dataManager.getReviews();
    }
    
    /**
     * Get all customers.
     * 
     * @return an ArrayList of all customers
     */
    public ArrayList<Customer> getAllCustomers() {
        return dataManager.getCustomers();
    }
    
    /**
     * Get all travel packages.
     * 
     * @return an ArrayList of all travel packages
     */
    public ArrayList<TravelPackage> getAllTravelPackages() {
        return dataManager.getPackages();
    }
    
    /**
     * Get all bookings.
     * 
     * @return an ArrayList of all bookings
     */
    public ArrayList<Booking> getAllBookings() {
        return dataManager.getBookings();
    }
    
    /**
     * Search for travel packages by destination.
     * 
     * @param destination the destination to search for
     * @return an ArrayList of matching travel packages
     */
    public ArrayList<TravelPackage> searchPackagesByDestination(String destination) {
        ArrayList<TravelPackage> result = new ArrayList<>();
        
        for (TravelPackage pkg : dataManager.getPackages()) {
            if (pkg.getDestination().toLowerCase().contains(destination.toLowerCase())) {
                result.add(pkg);
            }
        }
        
        return result;
    }
    
    /**
     * Search for bookings by status.
     * 
     * @param status the booking status to search for
     * @return an ArrayList of matching bookings
     */
    public ArrayList<Booking> searchBookingsByStatus(BookingStatus status) {
        ArrayList<Booking> result = new ArrayList<>();
        
        for (Booking booking : dataManager.getBookings()) {
            if (booking.getStatus() == status) {
                result.add(booking);
            }
        }
        
        return result;
    }
    
    /**
     * Search for customers by name.
     * 
     * @param name the name to search for
     * @return an ArrayList of matching customers
     */
    public ArrayList<Customer> searchCustomersByName(String name) {
        ArrayList<Customer> result = new ArrayList<>();
        
        for (Customer customer : dataManager.getCustomers()) {
            if (customer.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(customer);
            }
        }
        
        return result;
    }
    
    /**
     * Delete a travel package.
     * 
     * @param travelPackage the travel package to delete
     * @return true if the package was successfully deleted
     */
    public boolean deletePackage(TravelPackage travelPackage) {
        if (travelPackage == null) {
            return false;
        }
        
        // Check if package has any bookings
        for (Booking booking : dataManager.getBookings()) {
            if (booking.getService().equals(travelPackage)) {
                return false; // Can't delete a package that has bookings
            }
        }
        
        // Remove package from data manager
        boolean result = dataManager.removePackage(travelPackage);
        if (result) {
            saveData(); // Save changes to disk
        }
        return result;
    }
    
    /**
     * Delete a customer.
     * 
     * @param customer the customer to delete
     * @return true if the customer was successfully deleted
     */
    public boolean deleteCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }
        
        // Check if customer has any bookings
        for (Booking booking : dataManager.getBookings()) {
            if (booking.getCustomer().equals(customer)) {
                return false; // Can't delete a customer with bookings
            }
        }
        
        // Remove customer from data manager
        boolean result = dataManager.removeCustomer(customer);
        if (result) {
            saveData(); // Save changes to disk
        }
        return result;
    }
    
    /**
     * Check if a customer has any bookings.
     * 
     * @param customer the customer to check
     * @return true if the customer has bookings, false otherwise
     */
    public boolean isCustomerInUse(Customer customer) {
        if (customer == null) {
            return false;
        }
        
        return !getBookingsForCustomer(customer).isEmpty();
    }
    
    /**
     * Get all bookings for a specific customer.
     * 
     * @param customer the customer
     * @return list of bookings for the customer
     */
    public List<Booking> getBookingsForCustomer(Customer customer) {
        if (customer == null) {
            return new ArrayList<>();
        }
        
        return dataManager.getAllBookings().stream()
                .filter(booking -> booking.getCustomer().equals(customer))
                .collect(Collectors.toList());
    }
    
    /**
     * Update customer details.
     * 
     * @param customer the customer to update
     * @param name new name
     * @param email new email
     * @param phone new phone
     * @param address new address
     * @return true if update was successful
     */
    public boolean updateCustomer(Customer customer, String name, String email, String phone, String address) {
        if (customer == null) {
            return false;
        }
        
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        
        return true;
    }
    
    /**
     * Update a travel package.
     * 
     * @param travelPackage the package to update
     * @param name new name
     * @param description new description
     * @param basePrice new base price
     * @param destination new destination
     * @param duration new duration
     * @param accommodation new accommodation
     * @return true if update was successful
     */
    public boolean updateTravelPackage(TravelPackage travelPackage, String name, String description, 
                                    double basePrice, String destination, int duration, String accommodation) {
        if (travelPackage == null) {
            return false;
        }
        
        travelPackage.setName(name);
        travelPackage.setDescription(description);
        travelPackage.setBasePrice(basePrice);
        travelPackage.setDestination(destination);
        travelPackage.setDuration(duration);
        travelPackage.setAccommodation(accommodation);
        
        return true;
    }
    
    /**
     * Check if a travel package has any bookings.
     * 
     * @param travelPackage the package to check
     * @return true if the package has bookings, false otherwise
     */
    public boolean isPackageInUse(TravelPackage travelPackage) {
        if (travelPackage == null) {
            return false;
        }
        
        return !getBookingsForPackage(travelPackage).isEmpty();
    }
    
    /**
     * Get all bookings for a specific travel package.
     * 
     * @param travelPackage the travel package
     * @return list of bookings for the package
     */
    public List<Booking> getBookingsForPackage(TravelPackage travelPackage) {
        if (travelPackage == null) {
            return new ArrayList<>();
        }
        
        return dataManager.getAllBookings().stream()
                .filter(booking -> {
                    TravelPackage pkg = booking.getPackage();
                    return pkg != null && pkg.equals(travelPackage);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a booking.
     * 
     * @param booking the booking to delete
     * @return true if deletion was successful
     */
    public boolean deleteBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        
        return dataManager.removeBooking(booking);
    }
    
    /**
     * Delete a travel package.
     * 
     * @param travelPackage the package to delete
     * @return true if deletion was successful
     */
    public boolean deleteTravelPackage(TravelPackage travelPackage) {
        if (travelPackage == null || isPackageInUse(travelPackage)) {
            return false;
        }
        
        return dataManager.removePackage(travelPackage);
    }
    
    /**
     * Update a booking.
     * 
     * @param booking the booking to update
     * @param numTravelers new number of travelers
     * @param status new status
     * @param specialRequests new special requests
     * @return true if update was successful
     */
    public boolean updateBooking(Booking booking, int numTravelers, BookingStatus status, String specialRequests) {
        if (booking == null) {
            return false;
        }
        
        booking.setNumTravelers(numTravelers);
        booking.updateStatus(status);
        booking.setSpecialRequests(specialRequests);
        
        return true;
    }
    
    /**
     * Update just the status of a booking.
     * 
     * @param booking the booking to update
     * @param status the new status
     * @return true if update was successful
     */
    public boolean updateBookingStatus(Booking booking, BookingStatus status) {
        if (booking == null) {
            return false;
        }
        
        booking.updateStatus(status);
        return true;
    }
} 