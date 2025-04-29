package tams.model;

import java.util.ArrayList;
import tams.model.interfaces.Bookable;

/**
 * Represents a custom trip created for a specific customer.
 * Extends TravelService and implements Bookable interface.
 */
public class CustomTrip extends TravelService implements Bookable {
    
    private Customer customer;
    private ArrayList<String> destinations;
    private ArrayList<TravelService> services;
    private boolean available = true;
    
    /**
     * Constructor for creating a new custom trip.
     * 
     * @param serviceId the unique identifier for this trip
     * @param name the name of this trip
     * @param description the description of this trip
     * @param basePrice the base price of this trip
     * @param customer the customer for whom this trip is created
     */
    public CustomTrip(String serviceId, String name, String description, double basePrice, Customer customer) {
        super(serviceId, name, description, basePrice);
        this.customer = customer;
        this.destinations = new ArrayList<>();
        this.services = new ArrayList<>();
    }
    
    /**
     * Add a destination to this custom trip.
     * 
     * @param destination the destination to add
     */
    public void addDestination(String destination) {
        if (!destinations.contains(destination)) {
            destinations.add(destination);
        }
    }
    
    /**
     * Add a service to this custom trip.
     * 
     * @param service the service to add
     */
    public void addService(TravelService service) {
        if (!services.contains(service)) {
            services.add(service);
        }
    }
    
    /**
     * Overloaded method to add multiple services.
     * 
     * @param servicesToAdd list of services to add
     */
    public void addService(ArrayList<TravelService> servicesToAdd) {
        for (TravelService service : servicesToAdd) {
            addService(service);
        }
    }
    
    @Override
    public double calculateTotalPrice() {
        double totalPrice = basePrice;
        for (TravelService service : services) {
            totalPrice += service.calculateTotalPrice();
        }
        return totalPrice;
    }
    
    @Override
    public double calculatePrice() {
        return calculateTotalPrice();
    }
    
    @Override
    public boolean checkAvailability() {
        // Check if all included services are available
        for (TravelService service : services) {
            if (service instanceof Bookable) {
                if (!((Bookable) service).checkAvailability()) {
                    return false;
                }
            }
        }
        return available;
    }
    
    @Override
    public boolean makeReservation() {
        if (!checkAvailability()) {
            return false;
        }
        
        // Try to make reservations for all included bookable services
        for (TravelService service : services) {
            if (service instanceof Bookable) {
                if (!((Bookable) service).makeReservation()) {
                    // If any reservation fails, we should ideally rollback previous ones
                    // For simplicity, we'll just return false here
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // Getters and setters
    
    public Customer getCustomer() {
        return customer;
    }
    
    public ArrayList<String> getDestinations() {
        return new ArrayList<>(destinations);
    }
    
    public ArrayList<TravelService> getServices() {
        return new ArrayList<>(services);
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return String.format("Custom Trip: %s - %s - $%.2f", 
            getName(), String.join(", ", destinations), calculateTotalPrice());
    }
} 