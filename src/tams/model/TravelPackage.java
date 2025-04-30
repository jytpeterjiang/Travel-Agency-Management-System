package tams.model;

import java.util.ArrayList;
import tams.model.interfaces.Bookable;
import tams.model.interfaces.Reviewable;

/**
 * Represents a travel package that can be booked and reviewed.
 * Extends TravelService and implements both Bookable and Reviewable interfaces.
 */
public class TravelPackage extends TravelService implements Bookable, Reviewable {
    
    private String destination;
    private int duration; // Duration in days
    private String accommodation;
    private ArrayList<Activity> activities;
    private ArrayList<Review> reviews;
    private Itinerary itinerary;
    private boolean available = true;
    
    /**
     * Constructor for creating a new travel package.
     * 
     * @param serviceId the unique identifier for this package
     * @param name the name of this package
     * @param description the description of this package
     * @param basePrice the base price of this package
     * @param destination the destination of this package
     * @param duration the duration in days
     * @param accommodation the accommodation details
     */
    public TravelPackage(String serviceId, String name, String description, double basePrice,
                        String destination, int duration, String accommodation) {
        super(serviceId, name, description, basePrice);
        this.destination = destination;
        this.duration = duration;
        this.accommodation = accommodation;
        this.activities = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.itinerary = new Itinerary(serviceId + "-itinerary", this.name + " Itinerary");
    }
    
    /**
     * Overloaded constructor with initial activities.
     */
    public TravelPackage(String serviceId, String name, String description, double basePrice,
                        String destination, int duration, String accommodation, 
                        ArrayList<Activity> activities) {
        this(serviceId, name, description, basePrice, destination, duration, accommodation);
        if (activities != null) {
            this.activities.addAll(activities);
        }
    }
    
    /**
     * Add an activity to this package.
     * 
     * @param activity the activity to add
     */
    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }
    
    @Override
    public double calculateTotalPrice() {
        double activityCost = 0;
        for (Activity activity : activities) {
            activityCost += activity.getCost();
        }
        return basePrice + activityCost;
    }
    
    /**
     * Get the price of this package.
     * This is an alias for calculateTotalPrice().
     * 
     * @return the total price of the package
     */
    public double getPrice() {
        return calculateTotalPrice();
    }
    
    @Override
    public double calculatePrice() {
        return calculateTotalPrice();
    }
    
    @Override
    public boolean checkAvailability() {
        return available;
    }
    
    @Override
    public boolean makeReservation() {
        if (available) {
            // In a real system, this would update availability based on capacity
            return true;
        }
        return false;
    }
    
    @Override
    public void addReview(Review review) {
        if (!reviews.contains(review)) {
            reviews.add(review);
        }
    }
    
    @Override
    public double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0;
        }
        
        double totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();
    }
    
    @Override
    public ArrayList<Review> getReviews() {
        return new ArrayList<>(reviews);
    }
    
    public Itinerary getItinerary() {
        return itinerary;
    }
    
    // Getters and setters
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public String getAccommodation() {
        return accommodation;
    }
    
    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }
    
    /**
     * Get the activities in this package.
     * 
     * @return the list of activities
     */
    public ArrayList<Activity> getActivities() {
        return activities;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%d days) - $%.2f", 
            getName(), destination, duration, calculateTotalPrice());
    }
} 