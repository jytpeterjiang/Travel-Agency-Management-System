package tams.model;

/**
 * Represents an activity that can be part of a travel package or itinerary.
 */
public class Activity {
    
    private String activityId;
    private String name;
    private String location;
    private int duration; // Duration in hours
    private double cost;
    
    /**
     * Constructor for creating a new activity.
     * 
     * @param activityId the unique identifier for this activity
     * @param name the name of this activity
     * @param location the location where this activity takes place
     * @param duration the duration in hours
     * @param cost the cost of this activity
     */
    public Activity(String activityId, String name, String location, int duration, double cost) {
        this.activityId = activityId;
        this.name = name;
        this.location = location;
        this.duration = duration;
        this.cost = cost;
    }
    
    /**
     * Get the cost of this activity.
     * 
     * @return the cost as a double
     */
    public double getCost() {
        return cost;
    }
    
    // Getters and setters
    
    public String getActivityId() {
        return activityId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Activity other = (Activity) obj;
        return activityId.equals(other.activityId);
    }
    
    @Override
    public int hashCode() {
        return activityId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%d hours) - $%.2f", 
            name, location, duration, cost);
    }
} 