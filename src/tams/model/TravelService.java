package tams.model;

/**
 * Abstract class representing a travel service.
 * This serves as a base class for all travel services offered.
 */
public abstract class TravelService {
    
    /** The unique identifier for this service */
    protected String serviceId;
    
    /** The name of this service */
    protected String name;
    
    /** A description of what this service offers */
    protected String description;
    
    /** The base price of this service, before any additional calculations */
    protected double basePrice;
    
    /**
     * Constructor for creating a new travel service.
     * 
     * @param serviceId the unique identifier for this service
     * @param name the name of this service
     * @param description the description of this service
     * @param basePrice the base price of this service
     */
    public TravelService(String serviceId, String name, String description, double basePrice) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
    }
    
    /**
     * Abstract method to calculate the total price for this service.
     * Each subclass must implement its own pricing logic.
     * 
     * @return the total price as a double
     */
    public abstract double calculateTotalPrice();
    
    // Getters and setters
    
    public String getServiceId() {
        return serviceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
    
    @Override
    public String toString() {
        return name + " (" + serviceId + ")";
    }
} 